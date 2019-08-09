/**
 * Created by Hekx on 16/3/1.
 */
;(function (window, factory) {
    'use strict';
    if (typeof define === 'function' && define.amd) {
        define([], function () {
            return factory.call(window);
        });
    }
    if (typeof exports === 'object' && typeof module === 'object') {
        module.exports = factory.call(window);
    } else {
        window.sugarTpl = factory.call(window, document);
    }

})(typeof global === 'object' ? global : window, function () {
    'use strict';
    var sugarTemplate,
        sugarTemplateSettings = {
            evaluate: /(\{@)|(@\/\w+\})/g,
            tagStart: /(\{@)/g,
            tagClose: /(\}@\/\w+\})/g,
            tagConnection: /(\})@\/\w+.\{@(\w+)(\{)/g,
            connection: /\}@\/\w+\}\{@\w+\{/g,
            sentenceStart: /\{@(.\w+.+)/g,
            sentenceEnd: /\}@\/\w+./g,
            variant: /\$\{([\s\S]*?)\}/g,
            variantExp: /^\${|}/g
        },
        SugarTemplate = function (str, config) {
            if (typeof config !== 'undefined' && typeof config === 'string') {
                var variant = new RegExp(config + '\\{([\\s\\S]*?)\\}', 'g'),
                    variantExp = new RegExp('^' + config + '{|}', 'g');
                sugarTemplateSettings.variant = variant;
                sugarTemplateSettings.variantExp = variantExp;
            }
            this.template = str.replace(/(^\s+)|(\s+$)/g, '');

        };
    SugarTemplate.fn = SugarTemplate.prototype;

    SugarTemplate.fn.version = '0.0.1';
    /**
     *
     * @returns {T}
     */
    SugarTemplate.extend = function () {
        var arg = [].slice.call(arguments);
        if (typeof arg[0] !== 'object')return arg[0];
        for (var attr in arg[1]) {
            arg[0][attr] = typeof arg[1][attr] === 'object' ? this.extend(arg[1][attr]) : arg[1][attr];
        }
    };
    /**
     *
     * @type {{_cache: null, _parser: methodsSugarTpl._parser, compile: methodsSugarTpl.compile}}
     */
    var methodsSugarTpl = {
        _cache: null,
        /**
         *
         * @param data
         * @returns {*}
         * @private
         */
        _parser: function (data) {
            var templateEngine,
                template,
                exp = sugarTemplateSettings;
            /**
             *
             * @type {string|XML}
             */
            template = this.template.replace(/\{+\{/g, '{').replace(/\}\}+/g, '}').replace(/\\/g, '\\\\').replace(/(?="|')/g, '\\')
                .replace(/(\}@\/\w+\})(\s)(\{@\w+)(\s)(\{)/g, '$1$3$5')
                .replace(/(\}@\/\w+\})(\s)(\{@\w+\{)/g, '$1$3')
                .replace(/(\}@\/\w+\})(\s)(\{@\w+)(\s)(\{)/g, '$1$3$5')
                .replace(/(\}@\/\w+\})(\{@\w+)(\s)(\{)/g, '$1$2$4')
                .replace(exp.connection, function (code) {
                    code = code.replace(exp.tagConnection, '";$1$2$3');
                    return code + ';sugarBuildTemplate +="';
                })
                .replace(exp.tagStart, '";$1')
                .replace(/\{@+/g, ' {@ ')
                .replace(exp.sentenceStart, function (code) {
                    return code.replace(/\\/g, '') + ';sugarBuildTemplate +="';
                })
                .replace(exp.tagClose, '";$1')
                .replace(exp.sentenceEnd, function (code) {
                    return code + ';sugarBuildTemplate +="';
                })
                .replace(exp.evaluate, '')
                .replace(exp.variant, function (code) {
                    code = code.replace(exp.variantExp, '');
                    return '"+' + code.replace(/\\/g, '') + '+"';
                })
                .replace(/[\r\n\t]/g, ' ');
            template = ' "use strict"; var sugarBuildTemplate = "' + template + '"; return sugarBuildTemplate;';
            try {
                this._cache = templateEngine = new Function('data', template);
                return templateEngine(data);
            } catch (e) {
                try {
                    console.log(e);
                }catch (e){}
                return '<h3>SugarTpl Error:</h3><h4 style="color: red;">' + e.message + '</h4>';
            }
        },
        /**
         *
         * @param data
         * @param callback
         * @returns {*}
         */
        compile: function (data, callback) {
            var result;
            if (this._cache) {
                result = this._cache(data);
            } else {
                result = this._parser(data);
            }
            if (!!callback)callback(result);
            return result;
        }
    };
    SugarTemplate.extend(SugarTemplate.fn, methodsSugarTpl);
    /**
     *
     * @param str
     * @returns {*}
     */
    sugarTemplate = function (str, config) {
        if (str === '' || typeof str !== 'string')return false;
        return new SugarTemplate(str, config);
    };
    return sugarTemplate;
});
!(function (win, doc) {
    'use strict';
    var jsTpl = function (options) {
        var fun = options.onSuccess;
        jsTpl.prototype._init(options);
        if (fun) {
            fun();
        }
    };
    jsTpl.prototype = {
        version: '1.0',
        _init: function (opt) {
            var template = document.getElementById(opt.temlplateSrc).innerHTML;
            var view = document.getElementById(opt.viewTarget);
            var stpl = sugarTpl(template, opt.arg);
            var tpl = stpl.compile(opt.data);
            console.log(opt.data)
            view.innerHTML = tpl;
            return true;
        }
    };
    window.jsTpl = jsTpl;
}(window, document));