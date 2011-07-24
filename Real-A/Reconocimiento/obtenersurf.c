/*
 * PostgreSQL función que extrae los puntos caracteristicos mediante el algoritmo SURF.
 */

// Use 32-bit timer (provided header file uses 64-bit timer, not compatible with Windows postgreSQL versions 
#define _USE_32BIT_TIME_T

// Número de version de postgres
#define PG_VERSION_NUM 80404

// Número de version de visual C ++ usada para la compilación
#define _MSC_VER 1600


// Ensure that Pg_module_function and friends are declared __declspec(dllexport) */
#ifndef BUILDING_MODULE
  #define BUILDING_MODULE
#endif

#include <math.h>
#include "postgres.h"           // General Postgres declarations
#include "fmgr.h"               // Postgres function manager and function-call interface
#include "funcapi.h"
#include "abbrevs.h"
#include "lib/stringinfo.h"
#include "utils/typcache.h"     // For Type cache definitions
#include "access/tupmacs.h"     // Tuple macros used by both index tuples and heap tuples
#include "utils/array.h"        // Declarations for Postgres arrays.
#include <cv.h>					// Open CV, para el manejo de imagenes
#include <highgui.h>			
#include <string.h>
/*--------------- BEGIN REDEFINITION OF PG MACROS -------------------
 */
#undef PG_MODULE_MAGIC
#undef PG_FUNCTION_INFO_V1

#define PGMODULEEXPORT __declspec (dllexport)

#define PG_MODULE_MAGIC \
PGMODULEEXPORT const Pg_magic_struct * \
PG_MAGIC_FUNCTION_NAME(void) \
{ \
	static const Pg_magic_struct Pg_magic_data = PG_MODULE_MAGIC_DATA; \
	return &Pg_magic_data; \
} \
extern int no_such_variable

#define PG_FUNCTION_INFO_V1(funcname) \
PGMODULEEXPORT const Pg_finfo_record *  \
CppConcat(pg_finfo_,funcname) (void) \
{ \
	static const Pg_finfo_record my_finfo = { 1 }; \
	return &my_finfo; \
} \
extern int no_such_variable

/*--------------- END REDEFINITION OF PG MACROS -------------------*/

/* Define el tamaño de las imagenes*/
//const int tamanio_mayor = 320; 
//const int tamanio_menor = 240;
const int tamanio_mayor = 160; 
const int tamanio_menor = 120;

/* Constante para activar/desactivar mensajes NOTICE */
const bool mostrar_notices = false;

/*Descripcion PuntoClave */
typedef struct{
  float x, y;
  int scale;
  float orientation;
  int laplacian;
  float hessian;
  float descriptor[128];
} PuntoClave;

PG_MODULE_MAGIC;

//Funcion que obtiene los puntos SURF de la imagen
PuntoClave* obtenerPuntosDeImagen(IplImage *imagenEntrada, int *numeroPuntos){
	//Declaracion de variables
	CvMemStorage *storage = cvCreateMemStorage(0);  //Para la llamada al algoritmo SURF
	IplImage *imagen;	//Imagen
	CvSeq *keypoints_imagen = 0, *descriptors_imagen = 0;  //Definicion de puntos y descriptores
	CvSURFParams parametros = cvSURFParams(500, 1); //Parametros para SURF
	int numeroDePuntos; //Nº de puntos encontrados en la imagen
	int numeroDeDescri; //Nº de descriptores encontrados en la imagen
	int i;
	int j;
	PuntoClave* vectorPuntosClave;
	CvSize imgSize; // tamanio para escalar la imagen de entrada
    CvSeqReader reader;	//Para leer los descriptores
    int length; //Longitud descriptores

	if(mostrar_notices) ereport(NOTICE,(errmsg("obtenerPuntosDeImagen - Start")));
	//Se realiza el escalado de la imagen a tamanio_mayor x tamanio_menor
	if(imagenEntrada->width > imagenEntrada->height){
		imgSize.width = tamanio_mayor; // el tamaño de la imagen es
		imgSize.height = tamanio_menor; // tamanio_mayor x tamanio_menor pixels
	} else {
		imgSize.width = tamanio_menor; // el tamaño de la imagen es
		imgSize.height = tamanio_mayor; // tamanio_menor x tamanio_mayor pixels
	}
    imagen = cvCreateImage(imgSize, imagenEntrada->depth, imagenEntrada->nChannels);
	cvResize(imagenEntrada, imagen, CV_INTER_LINEAR);

	__try{
		cvExtractSURF( imagen, 0, &keypoints_imagen, &descriptors_imagen, storage, parametros, 0 );
	}__except(EXCEPTION_EXECUTE_HANDLER){
		ereport(ERROR, (111111, errmsg("Error al obtener puntos y descriptores de la imagen.")));
	}
     
	//constantes con el nº de puntos y descriptores encontrados	
	*numeroPuntos = numeroDePuntos = keypoints_imagen->total;
	numeroDeDescri = descriptors_imagen->total;
	
	//El nº de puntos y descriptores encontrados debe de coincidir 
	if(numeroDePuntos != numeroDeDescri){
		ereport(ERROR, (111111, errmsg("Error al obtener puntos y descriptores de la imagen.")));
    }

	//Definicion para leer los descriptores
    cvStartReadSeq( descriptors_imagen, &reader, 0 );
    length = (int)( descriptors_imagen->elem_size / sizeof(float) );

	vectorPuntosClave = (PuntoClave *) palloc ( numeroDePuntos * sizeof( PuntoClave ) );

	for (i=0;i < numeroDePuntos; i++){
		CvSURFPoint* keypoint;
		PuntoClave punto;
		float* mvec;

		//Lee el punto clave 
		keypoint=(CvSURFPoint*)cvGetSeqElem(keypoints_imagen,i);
		
		//Mapea los datos del punto
		punto.x = keypoint->pt.x;
		punto.y = keypoint->pt.y;
		punto.scale = keypoint->size;
		punto.orientation = keypoint->dir;
		punto.laplacian = keypoint->laplacian;
		punto.hessian = keypoint->hessian;
			
		//Lee el descriptor
		mvec = (float*)reader.ptr;
        CV_NEXT_SEQ_ELEM( reader.seq->elem_size, reader );
		for(j = 0; j < length; j ++ )
		{
			punto.descriptor[j] = mvec[j];
		}
		vectorPuntosClave[i] = punto;
	}
	
	if(mostrar_notices) ereport(NOTICE,(errmsg("obtenerPuntosDeImagen - End")));

	return(vectorPuntosClave);
}

char *convertir_array_descriptores(float descriptor[128]){	
	int				arr_size;	
	int				i;	
	StringInfoData	str;

	if(mostrar_notices) ereport(NOTICE,(errmsg("convertir_array_descriptores - Start")));

	initStringInfo(&str);

	arr_size = 128;	
	
	appendStringInfoChar(&str, '{');

	for(i=0; i < arr_size;i++){
		appendStringInfo(&str, "%f", descriptor[i]);					
		if (i != arr_size - 1){
			appendStringInfoChar(&str, ',');
		}
	}

	appendStringInfoChar(&str, '}');	

	if(mostrar_notices) ereport(NOTICE,(errmsg("convertir_array_descriptores - End")));

	return str.data;
}

PG_FUNCTION_INFO_V1(c_extraer_surf);

PGMODULEEXPORT Datum
c_extraer_surf(PG_FUNCTION_ARGS)
{
	//Declaración de variables
    FuncCallContext     *funcctx;
    int                  call_cntr;
    int                  max_calls;
    TupleDesc            tupdesc;
    AttInMetadata       *attinmeta;
	PuntoClave* vectorPuntosClave; //Vector con los puntos de la imagen

	if(mostrar_notices) ereport(NOTICE,(errmsg("c_extraer_surf - Start")));
    /* stuff done only on the first call of the function */
    if (SRF_IS_FIRSTCALL()) {
        MemoryContext   oldcontext;
		//Parametro de entrada, nombre del fichero a tratar
		const char* nombre_fichero_imagen = PG_GETARG_CSTRING(0);

		int numeroDePuntos;	//Numero de elementos que contiene el vector de puntos
		IplImage* imagen; //Imagen

		//Carga la imagen
		imagen = cvLoadImage( nombre_fichero_imagen, CV_LOAD_IMAGE_GRAYSCALE );

		//Error si la imagen no se ha podido cargar
		if( !imagen){
			ereport(ERROR, (111111, errmsg("El fichero no existe o no pudo ser cargado correctamente.")));
		}

		vectorPuntosClave = obtenerPuntosDeImagen(imagen, &numeroDePuntos);

		/* create a function context for cross-call persistence */
        funcctx = SRF_FIRSTCALL_INIT();

        /* switch to memory context appropriate for multiple function calls */
        oldcontext = MemoryContextSwitchTo(funcctx->multi_call_memory_ctx);

        /* total number of tuples to be returned */
        funcctx->max_calls = numeroDePuntos;
		funcctx->user_fctx = vectorPuntosClave;

        /* Build a tuple descriptor for our result type */
		if (get_call_result_type(fcinfo, NULL, &tupdesc) != TYPEFUNC_COMPOSITE){
            ereport(ERROR, (111111, errmsg("function returning record called in context that cannot accept type record")));
		}

		/*
         * generate attribute metadata needed later to produce tuples from raw
         * C strings
         */
        attinmeta = TupleDescGetAttInMetadata(tupdesc);
        funcctx->attinmeta = attinmeta;

        MemoryContextSwitchTo(oldcontext);
    }

    /* stuff done on every call of the function */
    funcctx = SRF_PERCALL_SETUP();

	vectorPuntosClave = funcctx->user_fctx;
    call_cntr = funcctx->call_cntr;
    max_calls = funcctx->max_calls;
    attinmeta = funcctx->attinmeta;

    if (call_cntr < max_calls)    /* do when there is more left to send */
    {
        char       **values;
        HeapTuple    tuple;
        Datum        result;
		int i;
		StringInfoData	str_punto;

		initStringInfo(&str_punto);

		appendStringInfo(&str_punto, "%f", vectorPuntosClave[call_cntr].x);					
		appendStringInfo(&str_punto, ":");					
		appendStringInfo(&str_punto, "%f", vectorPuntosClave[call_cntr].y);					
		appendStringInfo(&str_punto, ":");					
		appendStringInfo(&str_punto, "%d", vectorPuntosClave[call_cntr].scale);					
		appendStringInfo(&str_punto, ":");					
		appendStringInfo(&str_punto, "%f", vectorPuntosClave[call_cntr].orientation);					
		appendStringInfo(&str_punto, ":");					
		appendStringInfo(&str_punto, "%d", vectorPuntosClave[call_cntr].laplacian);					
		appendStringInfo(&str_punto, ":");					
		appendStringInfo(&str_punto, "%f", vectorPuntosClave[call_cntr].hessian);					
		appendStringInfo(&str_punto, ":");
		appendStringInfo(&str_punto, "%s", convertir_array_descriptores(vectorPuntosClave[call_cntr].descriptor));					
		appendStringInfo(&str_punto, ":");
		

        /*
         * Prepare a values array for building the returned tuple.
         * This should be an array of C strings which will
         * be processed later by the type input functions.
         */

		values = (char **) palloc(sizeof(char *));

        values[0] = str_punto.data;

        /* build a tuple */
        tuple = BuildTupleFromCStrings(attinmeta, values);
        
		/* make the tuple into a datum */
        result = HeapTupleGetDatum(tuple);

        /* clean up (this is not really necessary) */
		pfree(values[0]);
        pfree(values);

        SRF_RETURN_NEXT(funcctx, result);
    }
    else    /* do when there is no more left */
    {
		if(mostrar_notices) ereport(NOTICE,(errmsg("c_extraer_surf - End")));
        SRF_RETURN_DONE(funcctx);
    }
}

PG_FUNCTION_INFO_V1(c_distancia_cuadrada);

PGMODULEEXPORT Datum c_distancia_cuadrada(PG_FUNCTION_ARGS) {
    ArrayType*   vector1 = (ArrayType *) DatumGetPointer(PG_DETOAST_DATUM(PG_GETARG_DATUM(0)));
    ArrayType*   vector2 = (ArrayType *) DatumGetPointer(PG_DETOAST_DATUM(PG_GETARG_DATUM(1)));
    
    int32       length = ArrayGetNItems(ARR_NDIM(vector1), ARR_DIMS(vector1));   // array lengths
    
    float8*     ptr1 = (float8*) ARR_DATA_PTR(vector1);         // array data pointers
    float8*     ptr2 = (float8*) ARR_DATA_PTR(vector2);
    int32       pos = 0;            // array position
    float8      distance = 0;       // result

	//if(mostrar_notices) ereport(NOTICE,(errmsg("c_distancia_cuadrada - Start")));

    if (length != ArrayGetNItems(ARR_NDIM(vector2), ARR_DIMS(vector2))) {
        ereport(ERROR, (111111, errmsg("Los dos arrays deben tener la misma longitud")));
    }

    for (pos = 0; pos < length; pos++) {
        float8 diff = ptr1[pos] - ptr2[pos];
        distance += (diff * diff);
    }

	//if(mostrar_notices) ereport(NOTICE,(errmsg("c_distancia_cuadrada - End")));

	PG_RETURN_FLOAT8(distance);
}


double
compareSURFDescriptors( const float* d1, const float* d2, double best, int length )
{
    double total_cost = 0;
	int i;
		
	if(mostrar_notices) ereport(NOTICE,(errmsg("compareSURFDescriptors - Start")));

    for( i = 0; i < length; i += 4 ){
        double t0 = d1[i] - d2[i];
        double t1 = d1[i+1] - d2[i+1];
        double t2 = d1[i+2] - d2[i+2];
        double t3 = d1[i+3] - d2[i+3];
        total_cost += t0*t0 + t1*t1 + t2*t2 + t3*t3;
        if( total_cost > best ) break;
    }

	if(mostrar_notices) ereport(NOTICE,(errmsg("compareSURFDescriptors - End")));
	return total_cost;
}


int
naiveNearestNeighbor( const float* vec, int laplacian,
                      const CvSeq* model_keypoints,
                      const CvSeq* model_descriptors )
{
    int length = (int)(model_descriptors->elem_size/sizeof(float));
    int i, neighbor = -1;
    double d, dist1 = 1e6, dist2 = 1e6;
    CvSeqReader reader, kreader;

	if(mostrar_notices) ereport(NOTICE,(errmsg("naiveNearestNeighbor - Start")));

    cvStartReadSeq( model_keypoints, &kreader, 0 );
    cvStartReadSeq( model_descriptors, &reader, 0 );

    for( i = 0; i < model_descriptors->total; i++ )
    {
        const CvSURFPoint* kp = (const CvSURFPoint*)kreader.ptr;
        const float* mvec = (const float*)reader.ptr;
    	CV_NEXT_SEQ_ELEM( kreader.seq->elem_size, kreader );
        CV_NEXT_SEQ_ELEM( reader.seq->elem_size, reader );
        if( laplacian != kp->laplacian )
            continue;
        d = compareSURFDescriptors( vec, mvec, dist2, length );
        if( d < dist1 )
        {
            dist2 = dist1;
            dist1 = d;
            neighbor = i;
        }
        else if ( d < dist2 )
            dist2 = d;
    }
	
	if(mostrar_notices) ereport(NOTICE,(errmsg("naiveNearestNeighbor - End")));

    if ( dist1 < 0.6*dist2 )
        return neighbor;
    return -1;
}

PG_FUNCTION_INFO_V1(c_obtener_imagen_coincidencias);

PGMODULEEXPORT Datum
c_obtener_imagen_coincidencias(PG_FUNCTION_ARGS)
{
    const char* imagen1_filename = PG_GETARG_CSTRING(0);
    const char* imagen2_filename = PG_GETARG_CSTRING(1);
    const char* salida_filename  = PG_GETARG_CSTRING(2);

	CvMemStorage* storage = cvCreateMemStorage(0);

    static CvScalar color = {0,255,0};
   	CvSize imgSize1;   // tamanio para escalar la imagen de entrada1
	CvSize imgSize2;   // tamanio para escalar la imagen de entrada2
	CvSize salidaSize; // tamanio de la imagen de salida

	IplImage* imagenEntrada1 = cvLoadImage(imagen1_filename, CV_LOAD_IMAGE_GRAYSCALE );
    IplImage* imagenEntrada2 = cvLoadImage(imagen2_filename, CV_LOAD_IMAGE_GRAYSCALE );
	IplImage* imagen1;
	IplImage* imagen2;
	IplImage* salida;
    
	CvSeq *imagen1Keypoints = 0, *imagen1Descriptors = 0;
    CvSeq *imagen2Keypoints = 0, *imagen2Descriptors = 0;
    int i, coincidencias = 0;
	CvSeqReader reader, kreader;
	CvSURFParams parametros = cvSURFParams(500, 1);

	if(mostrar_notices) ereport(NOTICE,(errmsg("c_obtener_imagen_coincidencias - Start")));

	if( !imagenEntrada1 || !imagenEntrada2 ){
        ereport(ERROR, (111111, errmsg("Error al cargar imagenes")));
    }

    //Se realiza el escalado de la imagen1 a tamanio_mayor x tamanio_menor
	if(imagenEntrada1->width > imagenEntrada1->height){
		imgSize1.width  = tamanio_mayor; // el tamaño de la imagen es
		imgSize1.height = tamanio_menor; // tamanio_mayor x tamanio_menor pixels
	} else {
		imgSize1.width  = tamanio_menor; // el tamaño de la imagen es
		imgSize1.height = tamanio_mayor; // tamanio_menor x tamanio_mayor pixels
	}

	//Se realiza el escalado de la imagen1 a tamanio_mayor x tamanio_menor
	if(imagenEntrada2->width > imagenEntrada2->height){
		imgSize2.width  = tamanio_mayor; // el tamaño de la imagen es
		imgSize2.height = tamanio_menor; // tamanio_mayor x tamanio_menor pixels
	} else {
		imgSize2.width  = tamanio_menor; // el tamaño de la imagen es
		imgSize2.height = tamanio_mayor; // tamanio_menor x tamanio_mayor pixels
	}

	if(imagenEntrada1->width <= imagenEntrada2->width){
	    imagen1 = cvCreateImage(imgSize1, 8, 1);
		cvResize(imagenEntrada1, imagen1, CV_INTER_LINEAR);
	    imagen2 = cvCreateImage(imgSize2, 8, 1);
		cvResize(imagenEntrada2, imagen2, CV_INTER_LINEAR);
	} else { 
		imagen2 = cvCreateImage(imgSize1, 8, 1);
		cvResize(imagenEntrada1, imagen2, CV_INTER_LINEAR);
	    imagen1 = cvCreateImage(imgSize2, 8, 1);
		cvResize(imagenEntrada2, imagen1, CV_INTER_LINEAR);
	}

	__try{
		cvExtractSURF(imagen1, 0, &imagen1Keypoints, &imagen1Descriptors, storage, parametros, 0);
		cvExtractSURF(imagen2, 0, &imagen2Keypoints, &imagen2Descriptors, storage, parametros, 0);
	}__except(EXCEPTION_EXECUTE_HANDLER){
		ereport(ERROR, (111111, errmsg("Error al obtener puntos y descriptores de la imagen.")));
	}


	salidaSize.width = imagen1->width > imagen2->width ? imagen1->width : imagen2->width;
    salidaSize.height = imagen1->height + imagen2->height;	
	salida = cvCreateImage(salidaSize, 8, 1);
    cvSetImageROI(salida, cvRect(0, 0, imagen1->width, imagen1->height));
    cvCopy(imagen1, salida, 0);
    cvSetImageROI(salida, cvRect(0, imagen1->height, salida->width, salida->height));
	cvCopy(imagen2, salida, 0);
    cvResetImageROI(salida);
   
    cvStartReadSeq(imagen1Keypoints, &kreader, 0);
    cvStartReadSeq(imagen1Descriptors, &reader, 0);

    for(i = 0; i < imagen1Descriptors->total; i++){
        const CvSURFPoint* kp = (const CvSURFPoint*)kreader.ptr;
        const float* descriptor = (const float*)reader.ptr;
		int nearest_neighbor;

        CV_NEXT_SEQ_ELEM(kreader.seq->elem_size, kreader);
        CV_NEXT_SEQ_ELEM(reader.seq->elem_size, reader);
        nearest_neighbor = naiveNearestNeighbor(descriptor, kp->laplacian, imagen2Keypoints, imagen2Descriptors);
        if(nearest_neighbor >= 0){
	        CvSURFPoint* r1 = (CvSURFPoint*)cvGetSeqElem(imagen1Keypoints, i);
		    CvSURFPoint* r2 = (CvSURFPoint*)cvGetSeqElem(imagen2Keypoints, nearest_neighbor);
			cvLine(salida, cvPointFrom32f(r1->pt), cvPoint(cvRound(r2->pt.x), cvRound(r2->pt.y+imagen1->height)), color, 1, 8, 0);
			coincidencias++;
        }
    }

	cvSaveImage(salida_filename, salida, 0);

	if(mostrar_notices) ereport(NOTICE,(errmsg("c_obtener_imagen_coincidencias - End")));

	PG_RETURN_INT16(coincidencias);
}

PG_FUNCTION_INFO_V1(c_contar_coincidencias);

PGMODULEEXPORT Datum
c_contar_coincidencias(PG_FUNCTION_ARGS)
{
    const char* imagen1_filename = PG_GETARG_CSTRING(0);
    const char* imagen2_filename = PG_GETARG_CSTRING(1);
  
	CvMemStorage* storage = cvCreateMemStorage(0);

   	CvSize imgSize; // tamanio para escalar la imagen de entrada

	IplImage* imagenEntrada1 = cvLoadImage(imagen1_filename, CV_LOAD_IMAGE_GRAYSCALE );
    IplImage* imagenEntrada2 = cvLoadImage(imagen2_filename, CV_LOAD_IMAGE_GRAYSCALE );
	IplImage* imagen1;
	IplImage* imagen2;
    
	CvSeq *imagen1Keypoints = 0, *imagen1Descriptors = 0;
    CvSeq *imagen2Keypoints = 0, *imagen2Descriptors = 0;
    int i;
	int coincidencias = 0;
	CvSeqReader reader, kreader;
	CvSURFParams parametros = cvSURFParams(500, 1);

	if(mostrar_notices) ereport(NOTICE,(errmsg("c_contar_coincidencias - Start")));

	if( !imagenEntrada1 || !imagenEntrada2 ){
        ereport(ERROR, (111111, errmsg("Error al cargar imagenes")));
    }

    //Se realiza el escalado de la imagen1 a tamanio_mayor x tamanio_menor
	if(imagenEntrada1->width > imagenEntrada1->height){
		imgSize.width = tamanio_mayor; // el tamaño de la imagen es
		imgSize.height = tamanio_menor; // tamanio_mayor x tamanio_menor pixels
	} else {
		imgSize.width = tamanio_menor; // el tamaño de la imagen es
		imgSize.height = tamanio_mayor; // tamanio_menor x tamanio_mayor pixels
	}
    imagen1 = cvCreateImage(imgSize, imagenEntrada1->depth, imagenEntrada1->nChannels);
	cvResize(imagenEntrada1, imagen1, CV_INTER_LINEAR);

	    //Se realiza el escalado de la imagen1 a tamanio_mayor x tamanio_menor
	if(imagenEntrada2->width > imagenEntrada2->height){
		imgSize.width = tamanio_mayor; // el tamaño de la imagen es
		imgSize.height = tamanio_menor; // tamanio_mayor x tamanio_menor pixels
	} else {
		imgSize.width = tamanio_menor; // el tamaño de la imagen es
		imgSize.height = tamanio_mayor; // tamanio_menor x tamanio_mayor pixels
	}
    imagen2 = cvCreateImage(imgSize, imagenEntrada2->depth, imagenEntrada2->nChannels);
	cvResize(imagenEntrada2, imagen2, CV_INTER_LINEAR);

	__try{
		cvExtractSURF( imagen1, 0, &imagen1Keypoints, &imagen1Descriptors, storage, parametros, 0 );
	    cvExtractSURF( imagen2, 0, &imagen2Keypoints, &imagen2Descriptors, storage, parametros, 0);
	}__except(EXCEPTION_EXECUTE_HANDLER){
		ereport(ERROR, (111111, errmsg("Error al obtener puntos y descriptores de la imagen.")));
	}

    cvStartReadSeq( imagen1Keypoints, &kreader, 0 );
    cvStartReadSeq( imagen1Descriptors, &reader, 0 );

    for( i = 0; i < imagen1Descriptors->total; i++ ){
        const CvSURFPoint* kp = (const CvSURFPoint*)kreader.ptr;
        const float* descriptor = (const float*)reader.ptr;
		int nearest_neighbor;

        CV_NEXT_SEQ_ELEM( kreader.seq->elem_size, kreader );
        CV_NEXT_SEQ_ELEM( reader.seq->elem_size, reader );
        nearest_neighbor = naiveNearestNeighbor( descriptor, kp->laplacian, imagen2Keypoints, imagen2Descriptors );
        if( nearest_neighbor >= 0 ){
           coincidencias++;
		}
    }
	if(mostrar_notices) ereport(NOTICE,(errmsg("c_contar_coincidencias - End")));
	PG_RETURN_INT16(coincidencias);
}
