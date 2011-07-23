WRAjaxRequestQueue = Class.create( {

    initialize: function(options) {
        this.options = {};

        Object.extend(this.options, options || {});

        this.requests = [];
        this.flow = false;

        this.stackCount = 0;
    },

    add: function(obj) {
        if (!obj instanceof WRAjaxRequestQueue.DTO) {
            throw {
                message: "Error creating request"
            };
        }

        this.requests.push(obj);
    },

    start: function() {
        if (this.flow)
            return false;

        this.flow = true;
        this._send();
    },

    _send: function() {
        var obj = this.requests.shift();

        this.sendRequest(obj);
    },

    receiveRequest: function() {
        if (this.requests.length == 0) {
            this.flow = false;
        } else {
            this._send();
        }
    },

    sendRequest: function(dto) {
        var options = {
            onComplete: this.receiveRequest.bind(this)
        }
        Object.extend(options, dto.options || {});
        var form = $(options.formName);
        if (form && form.getInputs("file").size() > 0) {
            new WRAjax.FrameRequest(dto.url, options);
        } else {
            new Ajax.Request(dto.url, options);
        }
    }

});

WRAjaxRequestQueue.DTO = Class.create( {

    initialize: function(url, options) {
        this.url = url;
        this.options = options;
    }

});
