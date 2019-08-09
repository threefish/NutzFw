/// <reference path="jquery-1.9.1.intellisense.js" />
/// <reference path="jquery.ellipsis.js" />
/// author: Think Tam @2013-5-15

(function ($) {
    var $ellipsis = $.fn.ellipsis;

    $ellipsis.unobtrusive = {

        parseElement: function (element) {
            var $element = $(element);
            var maxWidth = $element.data('ellipsis-max-width');
            maxWidth = maxWidth ? parseInt(maxWidth) : 0;
            var maxLine = $element.data('ellipsis-max-line');
            maxLine = maxLine ? parseInt(maxLine) : 1;
            $element.ellipsis({ maxWidth: maxWidth, maxLine: maxLine });
        },

        parse: function (selector) {
            $(selector).find("[data-ellipsis=true]").each(function () {
                $ellipsis.unobtrusive.parseElement(this);
            });
        }
    };


    $(function () {    	
		var beginAt = new Date;
		if($ellipsis.debug){
			console.log(beginAt);
		}
		
        $ellipsis.unobtrusive.parse(document);
		
		if($ellipsis.debug){
			var endAt = new Date;
			console.log(endAt);
			console.log(endAt - beginAt);
		}
    });
}(jQuery)); 
