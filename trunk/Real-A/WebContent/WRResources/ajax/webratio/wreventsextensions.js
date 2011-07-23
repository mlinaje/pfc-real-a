var WREventUtils = {
    _fieldsValues: $H(),
    _focusedElement: null,
    _lastFocusedFields: {},

    saveFieldValue: function(field) {
        if ($(field).getAttribute("forcefocus") != "true") {
            this._fieldsValues.set(field, Form.Element.getValue(field));
        }
    },
    
    updateFocusedElement: function() {
        if (arguments[0]) {
            var field = Event.element(arguments[0]);
            this._focusedElement = field.id;
            if (field.form) {
                this._lastFocusedFields[field.form.id] = field.id;
            }
        } else {
            this._focusedElement = null;
        }
    },
    
    getFocusedElement: function() {
        return this._focusedElement;
    },
    
    getLastFocusedField: function(form) {
        form = $(form);
        return form ? $(this._lastFocusedFields[form.id]) : null;
    },
    
    forgetForm: function(form) {
        form = $(form);
        if (form) {
            delete(this._lastFocusedFields[form.id]);
        }
    },
    
    fieldValueChanged: function(field) {
        var storedValue = this._fieldsValues.get(field);
        var fieldValue = Form.Element.getValue(field);
        return storedValue != fieldValue;
    }
};

/**
 * Helper class for handling events by invoking an AJAX Event Link.
 */
var WREvent = {
	e: null,
	page: null,
	link: null,
	otherOptions: null,

	observe: function(event) {
		var options = $A(arguments);
		this.e = options[0];
		this.page = options[1];
		this.link = options[2];
		this.otherOptions = options[3];
		
		var alwaysFire = this.otherOptions.get("alwaysFire") || false;

		var elem = $(Event.element(event));
		var isField = elem.tagName.toLowerCase() == "input" || elem.tagName.toLowerCase() == "select" || elem.tagName.toLowerCase() == "textarea";
		if (isField && (alwaysFire || WREventUtils.fieldValueChanged(elem.id))) {
			if (event.type == "focus") {
				if (elem.id != fieldWithFocus) {
					WREvent.doEvent();
				}
				WREventUtils.updateFocusedElement(elem.id);
			} else if (event.type == "blur") {
				if (elem.id == WREventUtils.getFocusedElement()) {
					WREventUtils.updateFocusedElement(null);
				}
				WREvent.doEvent();
			} else {
				WREventUtils.saveFieldValue(elem.id);
				WREvent.doEvent();
			}
		}
		if (!isField) {
			WREvent.doEvent();
		}   
	},
         
	doEvent: function() {
		ajaxRequest(this.page + "FormBean", $H({sourcePage: this.page, isForm: true, pressedLink: "button#" + this.link}).merge(this.otherOptions))
	}
};

function RadioRequest(page,link,field,lastValue,otherOptions){
    ajaxRequest(page + 'FormBean', $H({'sourcePage': page, 'isForm': true, 'pressedLink' : 'button#' + link}).merge(otherOptions))
};

function CheckboxRequest(page,link,field,lastValue,otherOptions){
    ajaxRequest(page + 'FormBean', $H({'sourcePage': page, 'isForm': true, 'pressedLink' : 'button#' + link}).merge(otherOptions))
};

Form.Element.RadioEventObserver = Class.create(Abstract.EventObserver,{
   initialize: function(form, name, callback) {
    this.elements = Form.getInputs(form, 'radio', name);
    this.callback = callback;
    for (var i = 0; i < this.elements.length; i++) {
      this.registerCallback(this.elements[i]);
    }
  },
  
  onElementEvent: function(event) {
    var element = Event.element(event);

    this.callback(element, Form.Element.getValue(element)); 
  }
});


Form.Element.CheckboxEventObserver = Class.create(Abstract.EventObserver,{
  
  initialize: function(form, name, callback) {
    this.elements = Form.getInputs(form, 'checkbox', name);
    this.callback = callback;
    for (var i = 0; i < this.elements.length; i++) {
      this.registerCallback(this.elements[i]);
    }
  },
  
  onElementEvent: function(event) {
   var element = Event.element(event);

   this.callback(element, Form.Element.getValue(element)); 
  }
  
});