var multiEntryMap = $H();
var multiChoiceMap = $H();

function removeRow(unitID, rowID){
    var lastIndex = multiEntryMap.get(unitID + 'LastIndex');
      var lastID = unitID + '[' + lastIndex + ']'; 
      var row = document.getElementById(rowID);
      var ds = document.getElementById(unitID + 'DataSize');
      var size = parseInt(ds.getAttribute('value')); 
      if(size > 1){
         var pRow = row.previousSibling;
         while(pRow != null && pRow.id == null){
           pRow = pRow.previousSibling;
         } 
         if(pRow == null){
           var nextRow = row.nextSibling;
           
           while(nextRow != null && nextRow.id == null){
              nextRow = nextRow.nextSibling;
           } 
           var nextHeader = document.getElementById(nextRow.id + 'Header');
           if(navigator.product == "Gecko"){
              nextHeader.style.display='table-row';
           }else{
              nextHeader.style.display='inline';
           }
         }
         row.parentNode.removeChild(row);
         size--;
         ds.setAttribute('value', size); 

         if(lastID == rowID){
           lastIndex--;
           lastID = unitID + '[' + lastIndex + ']'; 
           while(document.getElementById(lastID) == null){
             lastIndex--;
             lastID = unitID + '[' + lastIndex + ']'; 
           }
         } 
       } 
       multiEntryMap.set(unitID + 'LastIndex', lastIndex);
}

function addRow(unitID){
    var lastIndex = multiEntryMap.get(unitID + 'LastIndex');
      var lastRowId =  unitID + '[' + lastIndex + ']';
      var lastRow = document.getElementById(lastRowId);
      if (!lastRow) {
        lastIndex = 0
        lastRowId =  unitID + '[0]';
        lastRow = document.getElementById(lastRowId);
      }
      var mainDiv = document.getElementById(unitID);
      var newRowId = unitID + '[' + ++lastIndex + ']';
      var temp = lastRow.innerHTML;
      while(temp.indexOf(lastRowId) > 0){
        temp = temp.replace(lastRowId, newRowId);
      }
      var newDiv = document.createElement('div');
      newDiv.setAttribute('id',newRowId);
      while(temp.indexOf("selected") > 0){
        temp = temp.replace("selected", "");
      }   
      newDiv.innerHTML = temp;
      mainDiv.appendChild(newDiv);
      var ds =  document.getElementById(unitID + 'DataSize');
      var size = parseInt(ds.getAttribute('value')) + 1;
      ds.setAttribute('value',size);
      if(size >= 2){
         var header = document.getElementById(newRowId + 'Header');
         if (header) {
            header.style.display='none';
         }
      }
      var inputs = document.getElementsByTagName('input'); 
      if(inputs != null){
          for(var i=0; i < inputs.length; i++){
             var input = inputs.item(i);
             var name = input.getAttribute('name');
             if(name!= null && name.indexOf(newRowId) > -1){
               input.setAttribute('value','');
             }
          }
      }
      multiEntryMap.set(unitID + 'LastIndex', lastIndex);
}

function checkall(unit, index, prefix, anchor, selectAll, unselectAll) {
    var key = multiChoiceMap.get(unit + "_" + index)
    var returnValue = false
    var fields = document.getElementsByName(unit + prefix);
    for(var i=0; i < fields.length; i++){
        if ((!key || key == "all") && fields[i].id.substring(fields[i].id.indexOf('_') + 1,fields[i].id.lastIndexOf('_')) == index ) {
            fields[i].checked = true;
        } else if (key == "none" && fields[i].id.substring(fields[i].id.indexOf('_') + 1,fields[i].id.lastIndexOf('_')) == index ) {
            fields[i].checked = false;
        }
    }
    if((!key || key == "all")){
        key = "none";
        if ($(unit + index + "image")) {
            $(unit + index + "image").className='unSelectAll'
        }
        if (anchor) {
            if (anchor.tagName.toLowerCase() == "a") {
                anchor.innerHTML = unselectAll
            } else if (anchor.tagName.toLowerCase() == "input") {
                returnValue = true
            }
        }
    } else {
        key = "all";
        if ($(unit + index + "image")) {
            $(unit + index + "image").className='selectAll'
        }
        if (anchor) {
            if (anchor.tagName.toLowerCase() == "a") {
                anchor.innerHTML = selectAll
            } else if (anchor.tagName.toLowerCase() == "input") {
                returnValue = true
            }
        }
    }       
    multiChoiceMap.set(unit + "_" + index, key)                 

    return returnValue
}

function selectAll(fieldId, status) {
    var elements = document.getElementsByName(fieldId);
    for (var i = 0; i < elements.length; i++) {
        var el = elements[i];
        if (el.tagName.toLowerCase() == "select") {
            for (var j = 0; j < el.options.length; j++) {
                el.options[j].selected = status;
            }
        } else if (el.tagName.toLowerCase() == "input") {
            el.checked = status;
        }
    }
}

function clickButton(id) {
   var ids = id.split("|");
   for(var i=0; i < ids.length; i++){
     var button = $('button#' + ids[i]);
     if(button != null && !button.disabled){
       button.click();
       break;
     }
   }
}

function forceFieldBlur(cal){
  cal.params.inputField.setAttribute("forcefocus", "true")
  cal.params.inputField.focus();
  cal.params.inputField.blur();
  cal.hide();
  setTimeout(function() {cal.params.inputField.setAttribute("forcefocus", "false")}, 100)
}

/*
 * Event extensions
 */
Event.observe = Event.observe.wrap(
    function($super, element, eventName, handler) {
        if (eventName == "dom:loaded" && element.tagName != "iframe") {
            var doc = element.ownerDocument || element;
            if (doc && doc.loaded) {
                if (handler) handler.defer();
                return element;
            }
        }
        return $super(element, eventName, handler);
    }
);

/*
 * Additional methods for all elements.
 * (do not move up: it needs method definitions from above)
 */
Element.addMethods({

    getOuterHTML: function(el) {
        if (el.outerHTML) {
            return el.outerHTML;
        } else {
            return "<" + el.tagName + ">" + el.innerHTML + "</" + el.tagName + ">";
        }
    },
    
    observe: Event.observe
    
});
Object.extend(document, {

    observe: Element.Methods.observe.methodize(),
    
    getTopPageForm: function() {
        if (document.forms.length > 0) {
            return document.forms[document.forms.length - 1];
        } else {
            return undefined;
        }
    }
    
});

/*
 * Page focus management
 */
document.observe("dom:loaded", function() {
    var popups = $A();
    
    if (window.Windows) {
        Windows.addObserver({
        
            onShow: function(eventName, win) {
                var i = popups.indexOf(win.getId());
                if (i < 0) {
                    popups.push(win.getId());
                    var form = this._getWindowForm(win);
                    if (form) {
                        Event.fire(form, "wr:pageFocus");
                    }
                }
            },
            
            onDestroy: function(eventName, win) {
                var i = popups.indexOf(win.getId());
                if (i >= 0) {
                    WREventUtils.forgetForm(this._getWindowForm(win));
                    popups.splice(i, 1);
                    var form;
                    if (popups.length > 0) {
                        var win = Windows.getWindow(popups[popups.length - 1]);
                        form = this._getWindowForm(win);
                    } else {
                        form = document.getTopPageForm();
                    }
                    if (form) {
                        Event.fire(form, "wr:pageFocus");
                    }
                }
            },
            
            _getWindowForm: function(win) {
                return $$("*[id='" + win.element.id + "'] form")[0];
            }
            
        });
    }

    var form = document.getTopPageForm();
    if (form) {
        Event.fire(form, "wr:pageFocus");
    }
});

