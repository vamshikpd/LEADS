/*!aaaaaaaaaaaa
 * jQuery JavaScript Library v1.3.2
 * http://jquery.com/
 *
 * Copyright (c) 2009 John Resig
 * Dual licensed under the MIT and GPL licenses.
 * http://docs.jquery.com/License
 *
 * Date: 2009-02-19 17:34:21 -0500 (Thu, 19 Feb 2009)
 * Revision: 6246
 */
(function(){

var 
	// Will speed up references to window, and allows munging its name.
	window = this,
	// Will speed up references to undefined, and allows munging its name.
	undefined,
	// Map over jQuery in case of overwrite
	_jQuery = window.jQuery,
	// Map over the $ in case of overwrite
	_$ = window.$,

	jQuery = window.jQuery = window.$ = function( selector, context ) {
		// The jQuery object is actually just the init constructor 'enhanced'
		return new jQuery.fn.init( selector, context );
	},

	// A simple way to check for HTML strings or ID strings
	// (both of which we optimize for)
	quickExpr = /^[^<]*(<(.|\s)+>)[^>]*$|^#([\w-]+)$/,
	// Is it a simple selector
	isSimple = /^.[^:#\[\.,]*$/;

jQuery.fn = jQuery.prototype = {
	init: function( selector, context ) {
		// Make sure that a selection was provided
		selector = selector || document;

		// Handle $(DOMElement)
		if ( selector.nodeType ) {
			this[0] = selector;
			this.length = 1;
			this.context = selector;
			return this;
		}
		// Handle HTML strings
		if ( typeof selector === "string" ) {
			// Are we dealing with HTML string or an ID?
			var match = quickExpr.exec( selector );

			// Verify a match, and that no context was specified for #id
			if ( match && (match[1] || !context) ) {

				// HANDLE: $(html) -> $(array)
				if ( match[1] )
					selector = jQuery.clean( [ match[1] ], context );

				// HANDLE: $("#id")
				else {
					var elem = document.getElementById( match[3] );

					// Handle the case where IE and Opera return items
					// by name instead of ID
					if ( elem && elem.id != match[3] )
						return jQuery().find( selector );

					// Otherwise, we inject the element directly into the jQuery object
					var ret = jQuery( elem || [] );
					ret.context = document;
					ret.selector = selector;
					return ret;
				}

			// HANDLE: $(expr, [context])
			// (which is just equivalent to: $(content).find(expr)
			} else
				return jQuery( context ).find( selector );

		// HANDLE: $(function)
		// Shortcut for document ready
		} else if ( jQuery.isFunction( selector ) )
			return jQuery( document ).ready( selector );

		// Make sure that old selector state is passed along
		if ( selector.selector && selector.context ) {
			this.selector = selector.selector;
			this.context = selector.context;
		}

		return this.setArray(jQuery.isArray( selector ) ?
			selector :
			jQuery.makeArray(selector));
	},

	// Start with an empty selector
	selector: "",

	// The current version of jQuery being used
	jquery: "1.3.2",

	// The number of elements contained in the matched element set
	size: function() {
		return this.length;
	},

	// Get the Nth element in the matched element set OR
	// Get the whole matched element set as a clean array
	get: function( num ) {
		return num === undefined ?

			// Return a 'clean' array
			Array.prototype.slice.call( this ) :

			// Return just the object
			this[ num ];
	},

	// Take an array of elements and push it onto the stack
	// (returning the new matched element set)
	pushStack: function( elems, name, selector ) {
		// Build a new jQuery matched element set
		var ret = jQuery( elems );

		// Add the old object onto the stack (as a reference)
		ret.prevObject = this;

		ret.context = this.context;

		if ( name === "find" )
			ret.selector = this.selector + (this.selector ? " " : "") + selector;
		else if ( name )
			ret.selector = this.selector + "." + name + "(" + selector + ")";

		// Return the newly-formed element set
		return ret;
	},

	// Force the current matched set of elements to become
	// the specified array of elements (destroying the stack in the process)
	// You should use pushStack() in order to do this, but maintain the stack
	setArray: function( elems ) {
		// Resetting the length to 0, then using the native Array push
		// is a super-fast way to populate an object with array-like properties
		this.length = 0;
		Array.prototype.push.apply( this, elems );

		return this;
	},

	// Execute a callback for every element in the matched set.
	// (You can seed the arguments with an array of args, but this is
	// only used internally.)
	each: function( callback, args ) {
		return jQuery.each( this, callback, args );
	},

	// Determine the position of an element within
	// the matched set of elements
	index: function( elem ) {
		// Locate the position of the desired element
		return jQuery.inArray(
			// If it receives a jQuery object, the first element is used
			elem && elem.jquery ? elem[0] : elem
		, this );
	},

	attr: function( name, value, type ) {
		var options = name;

		// Look for the case where we're accessing a style value
		if ( typeof name === "string" )
			if ( value === undefined )
				return this[0] && jQuery[ type || "attr" ]( this[0], name );

			else {
				options = {};
				options[ name ] = value;
			}

		// Check to see if we're setting style values
		return this.each(function(i){
			// Set all the styles
			for ( name in options )
				jQuery.attr(
					type ?
						this.style :
						this,
					name, jQuery.prop( this, options[ name ], type, i, name )
				);
		});
	},

	css: function( key, value ) {
		// ignore negative width and height values
		if ( (key == 'width' || key == 'height') && parseFloat(value) < 0 )
			value = undefined;
		return this.attr( key, value, "curCSS" );
	},

	text: function( text ) {
		if ( typeof text !== "object" && text != null )
			return this.empty().append( (this[0] && this[0].ownerDocument || document).createTextNode( text ) );

		var ret = "";

		jQuery.each( text || this, function(){
			jQuery.each( this.childNodes, function(){
				if ( this.nodeType != 8 )
					ret += this.nodeType != 1 ?
						this.nodeValue :
						jQuery.fn.text( [ this ] );
			});
		});

		return ret;
	},

	wrapAll: function( html ) {
		if ( this[0] ) {
			// The elements to wrap the target around
			var wrap = jQuery( html, this[0].ownerDocument ).clone();

			if ( this[0].parentNode )
				wrap.insertBefore( this[0] );

			wrap.map(function(){
				var elem = this;

				while ( elem.firstChild )
					elem = elem.firstChild;

				return elem;
			}).append(this);
		}

		return this;
	},

	wrapInner: function( html ) {
		return this.each(function(){
			jQuery( this ).contents().wrapAll( html );
		});
	},

	wrap: function( html ) {
		return this.each(function(){
			jQuery( this ).wrapAll( html );
		});
	},

	append: function() {
		return this.domManip(arguments, true, function(elem){
			if (this.nodeType == 1)
				this.appendChild( elem );
		});
	},

	prepend: function() {
		return this.domManip(arguments, true, function(elem){
			if (this.nodeType == 1)
				this.insertBefore( elem, this.firstChild );
		});
	},

	before: function() {
		return this.domManip(arguments, false, function(elem){
			this.parentNode.insertBefore( elem, this );
		});
	},

	after: function() {
		return this.domManip(arguments, false, function(elem){
			this.parentNode.insertBefore( elem, this.nextSibling );
		});
	},

	end: function() {
		return this.prevObject || jQuery( [] );
	},

	// For internal use only.
	// Behaves like an Array's method, not like a jQuery method.
	push: [].push,
	sort: [].sort,
	splice: [].splice,

	find: function( selector ) {
		if ( this.length === 1 ) {
			var ret = this.pushStack( [], "find", selector );
			ret.length = 0;
			jQuery.find( selector, this[0], ret );
			return ret;
		} else {
			return this.pushStack( jQuery.unique(jQuery.map(this, function(elem){
				return jQuery.find( selector, elem );
			})), "find", selector );
		}
	},

	clone: function( events ) {
		// Do the clone
		var ret = this.map(function(){
			if ( !jQuery.support.noCloneEvent && !jQuery.isXMLDoc(this) ) {
				// IE copies events bound via attachEvent when
				// using cloneNode. Calling detachEvent on the
				// clone will also remove the events from the orignal
				// In order to get around this, we use innerHTML.
				// Unfortunately, this means some modifications to
				// attributes in IE that are actually only stored
				// as properties will not be copied (such as the
				// the name attribute on an input).
				var html = this.outerHTML;
				if ( !html ) {
					var div = this.ownerDocument.createElement("div");
					div.appendChild( this.cloneNode(true) );
					html = div.innerHTML;
				}

				return jQuery.clean([html.replace(/ jQuery\d+="(?:\d+|null)"/g, "").replace(/^\s*/, "")])[0];
			} else
				return this.cloneNode(true);
		});

		// Copy the events from the original to the clone
		if ( events === true ) {
			var orig = this.find("*").andSelf(), i = 0;

			ret.find("*").andSelf().each(function(){
				if ( this.nodeName !== orig[i].nodeName )
					return;

				var events = jQuery.data( orig[i], "events" );

				for ( var type in events ) {
					for ( var handler in events[ type ] ) {
						jQuery.event.add( this, type, events[ type ][ handler ], events[ type ][ handler ].data );
					}
				}

				i++;
			});
		}

		// Return the cloned set
		return ret;
	},

	filter: function( selector ) {
		return this.pushStack(
			jQuery.isFunction( selector ) &&
			jQuery.grep(this, function(elem, i){
				return selector.call( elem, i );
			}) ||

			jQuery.multiFilter( selector, jQuery.grep(this, function(elem){
				return elem.nodeType === 1;
			}) ), "filter", selector );
	},

	closest: function( selector ) {
		var pos = jQuery.expr.match.POS.test( selector ) ? jQuery(selector) : null,
			closer = 0;

		return this.map(function(){
			var cur = this;
			while ( cur && cur.ownerDocument ) {
				if ( pos ? pos.index(cur) > -1 : jQuery(cur).is(selector) ) {
					jQuery.data(cur, "closest", closer);
					return cur;
				}
				cur = cur.parentNode;
				closer++;
			}
		});
	},

	not: function( selector ) {
		if ( typeof selector === "string" )
			// test special case where just one selector is passed in
			if ( isSimple.test( selector ) )
				return this.pushStack( jQuery.multiFilter( selector, this, true ), "not", selector );
			else
				selector = jQuery.multiFilter( selector, this );

		var isArrayLike = selector.length && selector[selector.length - 1] !== undefined && !selector.nodeType;
		return this.filter(function() {
			return isArrayLike ? jQuery.inArray( this, selector ) < 0 : this != selector;
		});
	},

	add: function( selector ) {
		return this.pushStack( jQuery.unique( jQuery.merge(
			this.get(),
			typeof selector === "string" ?
				jQuery( selector ) :
				jQuery.makeArray( selector )
		)));
	},

	is: function( selector ) {
		return !!selector && jQuery.multiFilter( selector, this ).length > 0;
	},

	hasClass: function( selector ) {
		return !!selector && this.is( "." + selector );
	},

	val: function( value ) {
		if ( value === undefined ) {			
			var elem = this[0];

			if ( elem ) {
				if( jQuery.nodeName( elem, 'option' ) )
					return (elem.attributes.value || {}).specified ? elem.value : elem.text;
				
				// We need to handle select boxes special
				if ( jQuery.nodeName( elem, "select" ) ) {
					var index = elem.selectedIndex,
						values = [],
						options = elem.options,
						one = elem.type == "select-one";

					// Nothing was selected
					if ( index < 0 )
						return null;

					// Loop through all the selected options
					for ( var i = one ? index : 0, max = one ? index + 1 : options.length; i < max; i++ ) {
						var option = options[ i ];

						if ( option.selected ) {
							// Get the specifc value for the option
							value = jQuery(option).val();

							// We don't need an array for one selects
							if ( one )
								return value;

							// Multi-Selects return an array
							values.push( value );
						}
					}

					return values;				
				}

				// Everything else, we just grab the value
				return (elem.value || "").replace(/\r/g, "");

			}

			return undefined;
		}

		if ( typeof value === "number" )
			value += '';

		return this.each(function(){
			if ( this.nodeType != 1 )
				return;

			if ( jQuery.isArray(value) && /radio|checkbox/.test( this.type ) )
				this.checked = (jQuery.inArray(this.value, value) >= 0 ||
					jQuery.inArray(this.name, value) >= 0);

			else if ( jQuery.nodeName( this, "select" ) ) {
				var values = jQuery.makeArray(value);

				jQuery( "option", this ).each(function(){
					this.selected = (jQuery.inArray( this.value, values ) >= 0 ||
						jQuery.inArray( this.text, values ) >= 0);
				});

				if ( !values.length )
					this.selectedIndex = -1;

			} else
				this.value = value;
		});
	},

	html: function( value ) {
		return value === undefined ?
			(this[0] ?
				this[0].innerHTML.replace(/ jQuery\d+="(?:\d+|null)"/g, "") :
				null) :
			this.empty().append( value );
	},

	replaceWith: function( value ) {
		return this.after( value ).remove();
	},

	eq: function( i ) {
		return this.slice( i, +i + 1 );
	},

	slice: function() {
		return this.pushStack( Array.prototype.slice.apply( this, arguments ),
			"slice", Array.prototype.slice.call(arguments).join(",") );
	},

	map: function( callback ) {
		return this.pushStack( jQuery.map(this, function(elem, i){
			return callback.call( elem, i, elem );
		}));
	},

	andSelf: function() {
		return this.add( this.prevObject );
	},

	domManip: function( args, table, callback ) {
		if ( this[0] ) {
			var fragment = (this[0].ownerDocument || this[0]).createDocumentFragment(),
				scripts = jQuery.clean( args, (this[0].ownerDocument || this[0]), fragment ),
				first = fragment.firstChild;

			if ( first )
				for ( var i = 0, l = this.length; i < l; i++ )
					callback.call( root(this[i], first), this.length > 1 || i > 0 ?
							fragment.cloneNode(true) : fragment );
		
			if ( scripts )
				jQuery.each( scripts, evalScript );
		}

		return this;
		
		function root( elem, cur ) {
			return table && jQuery.nodeName(elem, "table") && jQuery.nodeName(cur, "tr") ?
				(elem.getElementsByTagName("tbody")[0] ||
				elem.appendChild(elem.ownerDocument.createElement("tbody"))) :
				elem;
		}
	}
};

// Give the init function the jQuery prototype for later instantiation
jQuery.fn.init.prototype = jQuery.fn;

function evalScript( i, elem ) {
	if ( elem.src )
		jQuery.ajax({
			url: elem.src,
			async: false,
			dataType: "script"
		});

	else
		jQuery.globalEval( elem.text || elem.textContent || elem.innerHTML || "" );

	if ( elem.parentNode )
		elem.parentNode.removeChild( elem );
}

function now(){
	return +new Date;
}

jQuery.extend = jQuery.fn.extend = function() {
	// copy reference to target object
	var target = arguments[0] || {}, i = 1, length = arguments.length, deep = false, options;

	// Handle a deep copy situation
	if ( typeof target === "boolean" ) {
		deep = target;
		target = arguments[1] || {};
		// skip the boolean and the target
		i = 2;
	}

	// Handle case when target is a string or something (possible in deep copy)
	if ( typeof target !== "object" && !jQuery.isFunction(target) )
		target = {};

	// extend jQuery itself if only one argument is passed
	if ( length == i ) {
		target = this;
		--i;
	}

	for ( ; i < length; i++ )
		// Only deal with non-null/undefined values
		if ( (options = arguments[ i ]) != null )
			// Extend the base object
			for ( var name in options ) {
				var src = target[ name ], copy = options[ name ];

				// Prevent never-ending loop
				if ( target === copy )
					continue;

				// Recurse if we're merging object values
				if ( deep && copy && typeof copy === "object" && !copy.nodeType )
					target[ name ] = jQuery.extend( deep, 
						// Never move original objects, clone them
						src || ( copy.length != null ? [ ] : { } )
					, copy );

				// Don't bring in undefined values
				else if ( copy !== undefined )
					target[ name ] = copy;

			}

	// Return the modified object
	return target;
};

// exclude the following css properties to add px
var	exclude = /z-?index|font-?weight|opacity|zoom|line-?height/i,
	// cache defaultView
	defaultView = document.defaultView || {},
	toString = Object.prototype.toString;

jQuery.extend({
	noConflict: function( deep ) {
		window.$ = _$;

		if ( deep )
			window.jQuery = _jQuery;

		return jQuery;
	},

	// See test/unit/core.js for details concerning isFunction.
	// Since version 1.3, DOM methods and functions like alert
	// aren't supported. They return false on IE (#2968).
	isFunction: function( obj ) {
		return toString.call(obj) === "[object Function]";
	},

	isArray: function( obj ) {
		return toString.call(obj) === "[object Array]";
	},

	// check if an element is in a (or is an) XML document
	isXMLDoc: function( elem ) {
		return elem.nodeType === 9 && elem.documentElement.nodeName !== "HTML" ||
			!!elem.ownerDocument && jQuery.isXMLDoc( elem.ownerDocument );
	},

	// Evalulates a script in a global context
	globalEval: function( data ) {
		if ( data && /\S/.test(data) ) {
			// Inspired by code by Andrea Giammarchi
			// http://webreflection.blogspot.com/2007/08/global-scope-evaluation-and-dom.html
			var head = document.getElementsByTagName("head")[0] || document.documentElement,
				script = document.createElement("script");

			script.type = "text/javascript";
			if ( jQuery.support.scriptEval )
				script.appendChild( document.createTextNode( data ) );
			else
				script.text = data;

			// Use insertBefore instead of appendChild  to circumvent an IE6 bug.
			// This arises when a base node is used (#2709).
			head.insertBefore( script, head.firstChild );
			head.removeChild( script );
		}
	},

	nodeName: function( elem, name ) {
		return elem.nodeName && elem.nodeName.toUpperCase() == name.toUpperCase();
	},

	// args is for internal usage only
	each: function( object, callback, args ) {
		var name, i = 0, length = object.length;

		if ( args ) {
			if ( length === undefined ) {
				for ( name in object )
					if ( callback.apply( object[ name ], args ) === false )
						break;
			} else
				for ( ; i < length; )
					if ( callback.apply( object[ i++ ], args ) === false )
						break;

		// A special, fast, case for the most common use of each
		} else {
			if ( length === undefined ) {
				for ( name in object )
					if ( callback.call( object[ name ], name, object[ name ] ) === false )
						break;
			} else
				for ( var value = object[0];
					i < length && callback.call( value, i, value ) !== false; value = object[++i] ){}
		}

		return object;
	},

	prop: function( elem, value, type, i, name ) {
		// Handle executable functions
		if ( jQuery.isFunction( value ) )
			value = value.call( elem, i );

		// Handle passing in a number to a CSS property
		return typeof value === "number" && type == "curCSS" && !exclude.test( name ) ?
			value + "px" :
			value;
	},

	className: {
		// internal only, use addClass("class")
		add: function( elem, classNames ) {
			jQuery.each((classNames || "").split(/\s+/), function(i, className){
				if ( elem.nodeType == 1 && !jQuery.className.has( elem.className, className ) )
					elem.className += (elem.className ? " " : "") + className;
			});
		},

		// internal only, use removeClass("class")
		remove: function( elem, classNames ) {
			if (elem.nodeType == 1)
				elem.className = classNames !== undefined ?
					jQuery.grep(elem.className.split(/\s+/), function(className){
						return !jQuery.className.has( classNames, className );
					}).join(" ") :
					"";
		},

		// internal only, use hasClass("class")
		has: function( elem, className ) {
			return elem && jQuery.inArray( className, (elem.className || elem).toString().split(/\s+/) ) > -1;
		}
	},

	// A method for quickly swapping in/out CSS properties to get correct calculations
	swap: function( elem, options, callback ) {
		var old = {};
		// Remember the old values, and insert the new ones
		for ( var name in options ) {
			old[ name ] = elem.style[ name ];
			elem.style[ name ] = options[ name ];
		}

		callback.call( elem );

		// Revert the old values
		for ( var name in options )
			elem.style[ name ] = old[ name ];
	},

	css: function( elem, name, force, extra ) {
		if ( name == "width" || name == "height" ) {
			var val, props = { position: "absolute", visibility: "hidden", display:"block" }, which = name == "width" ? [ "Left", "Right" ] : [ "Top", "Bottom" ];

			function getWH() {
				val = name == "width" ? elem.offsetWidth : elem.offsetHeight;

				if ( extra === "border" )
					return;

				jQuery.each( which, function() {
					if ( !extra )
						val -= parseFloat(jQuery.curCSS( elem, "padding" + this, true)) || 0;
					if ( extra === "margin" )
						val += parseFloat(jQuery.curCSS( elem, "margin" + this, true)) || 0;
					else
						val -= parseFloat(jQuery.curCSS( elem, "border" + this + "Width", true)) || 0;
				});
			}

			if ( elem.offsetWidth !== 0 )
				getWH();
			else
				jQuery.swap( elem, props, getWH );

			return Math.max(0, Math.round(val));
		}

		return jQuery.curCSS( elem, name, force );
	},

	curCSS: function( elem, name, force ) {
		var ret, style = elem.style;

		// We need to handle opacity special in IE
		if ( name == "opacity" && !jQuery.support.opacity ) {
			ret = jQuery.attr( style, "opacity" );

			return ret == "" ?
				"1" :
				ret;
		}

		// Make sure we're using the right name for getting the float value
		if ( name.match( /float/i ) )
			name = styleFloat;

		if ( !force && style && style[ name ] )
			ret = style[ name ];

		else if ( defaultView.getComputedStyle ) {

			// Only "float" is needed here
			if ( name.match( /float/i ) )
				name = "float";

			name = name.replace( /([A-Z])/g, "-$1" ).toLowerCase();

			var computedStyle = defaultView.getComputedStyle( elem, null );

			if ( computedStyle )
				ret = computedStyle.getPropertyValue( name );

			// We should always get a number back from opacity
			if ( name == "opacity" && ret == "" )
				ret = "1";

		} else if ( elem.currentStyle ) {
			var camelCase = name.replace(/\-(\w)/g, function(all, letter){
				return letter.toUpperCase();
			});

			ret = elem.currentStyle[ name ] || elem.currentStyle[ camelCase ];

			// From the awesome hack by Dean Edwards
			// http://erik.eae.net/archives/2007/07/27/18.54.15/#comment-102291

			// If we're not dealing with a regular pixel number
			// but a number that has a weird ending, we need to convert it to pixels
			if ( !/^\d+(px)?$/i.test( ret ) && /^\d/.test( ret ) ) {
				// Remember the original values
				var left = style.left, rsLeft = elem.runtimeStyle.left;

				// Put in the new values to get a computed value out
				elem.runtimeStyle.left = elem.currentStyle.left;
				style.left = ret || 0;
				ret = style.pixelLeft + "px";

				// Revert the changed values
				style.left = left;
				elem.runtimeStyle.left = rsLeft;
			}
		}

		return ret;
	},

	clean: function( elems, context, fragment ) {
		context = context || document;

		// !context.createElement fails in IE with an error but returns typeof 'object'
		if ( typeof context.createElement === "undefined" )
			context = context.ownerDocument || context[0] && context[0].ownerDocument || document;

		// If a single string is passed in and it's a single tag
		// just do a createElement and skip the rest
		if ( !fragment && elems.length === 1 && typeof elems[0] === "string" ) {
			var match = /^<(\w+)\s*\/?>$/.exec(elems[0]);
			if ( match )
				return [ context.createElement( match[1] ) ];
		}

		var ret = [], scripts = [], div = context.createElement("div");

		jQuery.each(elems, function(i, elem){
			if ( typeof elem === "number" )
				elem += '';

			if ( !elem )
				return;

			// Convert html string into DOM nodes
			if ( typeof elem === "string" ) {
				// Fix "XHTML"-style tags in all browsers
				elem = elem.replace(/(<(\w+)[^>]*?)\/>/g, function(all, front, tag){
					return tag.match(/^(abbr|br|col|img|input|link|meta|param|hr|area|embed)$/i) ?
						all :
						front + "></" + tag + ">";
				});

				// Trim whitespace, otherwise indexOf won't work as expected
				var tags = elem.replace(/^\s+/, "").substring(0, 10).toLowerCase();

				var wrap =
					// option or optgroup
					!tags.indexOf("<opt") &&
					[ 1, "<select multiple='multiple'>", "</select>" ] ||

					!tags.indexOf("<leg") &&
					[ 1, "<fieldset>", "</fieldset>" ] ||

					tags.match(/^<(thead|tbody|tfoot|colg|cap)/) &&
					[ 1, "<table>", "</table>" ] ||

					!tags.indexOf("<tr") &&
					[ 2, "<table><tbody>", "</tbody></table>" ] ||

				 	// <thead> matched above
					(!tags.indexOf("<td") || !tags.indexOf("<th")) &&
					[ 3, "<table><tbody><tr>", "</tr></tbody></table>" ] ||

					!tags.indexOf("<col") &&
					[ 2, "<table><tbody></tbody><colgroup>", "</colgroup></table>" ] ||

					// IE can't serialize <link> and <script> tags normally
					!jQuery.support.htmlSerialize &&
					[ 1, "div<div>", "</div>" ] ||

					[ 0, "", "" ];

				// Go to html and back, then peel off extra wrappers
				div.innerHTML = wrap[1] + elem + wrap[2];

				// Move to the right depth
				while ( wrap[0]-- )
					div = div.lastChild;

				// Remove IE's autoinserted <tbody> from table fragments
				if ( !jQuery.support.tbody ) {

					// String was a <table>, *may* have spurious <tbody>
					var hasBody = /<tbody/i.test(elem),
						tbody = !tags.indexOf("<table") && !hasBody ?
							div.firstChild && div.firstChild.childNodes :

						// String was a bare <thead> or <tfoot>
						wrap[1] == "<table>" && !hasBody ?
							div.childNodes :
							[];

					for ( var j = tbody.length - 1; j >= 0 ; --j )
						if ( jQuery.nodeName( tbody[ j ], "tbody" ) && !tbody[ j ].childNodes.length )
							tbody[ j ].parentNode.removeChild( tbody[ j ] );

					}

				// IE completely kills leading whitespace when innerHTML is used
				if ( !jQuery.support.leadingWhitespace && /^\s/.test( elem ) )
					div.insertBefore( context.createTextNode( elem.match(/^\s*/)[0] ), div.firstChild );
				
				elem = jQuery.makeArray( div.childNodes );
			}

			if ( elem.nodeType )
				ret.push( elem );
			else
				ret = jQuery.merge( ret, elem );

		});

		if ( fragment ) {
			for ( var i = 0; ret[i]; i++ ) {
				if ( jQuery.nodeName( ret[i], "script" ) && (!ret[i].type || ret[i].type.toLowerCase() === "text/javascript") ) {
					scripts.push( ret[i].parentNode ? ret[i].parentNode.removeChild( ret[i] ) : ret[i] );
				} else {
					if ( ret[i].nodeType === 1 )
						ret.splice.apply( ret, [i + 1, 0].concat(jQuery.makeArray(ret[i].getElementsByTagName("script"))) );
					fragment.appendChild( ret[i] );
				}
			}
			
			return scripts;
		}

		return ret;
	},

	attr: function( elem, name, value ) {
		// don't set attributes on text and comment nodes
		if (!elem || elem.nodeType == 3 || elem.nodeType == 8)
			return undefined;

		var notxml = !jQuery.isXMLDoc( elem ),
			// Whether we are setting (or getting)
			set = value !== undefined;

		// Try to normalize/fix the name
		name = notxml && jQuery.props[ name ] || name;

		// Only do all the following if this is a node (faster for style)
		// IE elem.getAttribute passes even for style
		if ( elem.tagName ) {

			// These attributes require special treatment
			var special = /href|src|style/.test( name );

			// Safari mis-reports the default selected property of a hidden option
			// Accessing the parent's selectedIndex property fixes it
			if ( name == "selected" && elem.parentNode )
				elem.parentNode.selectedIndex;

			// If applicable, access the attribute via the DOM 0 way
			if ( name in elem && notxml && !special ) {
				if ( set ){
					// We can't allow the type property to be changed (since it causes problems in IE)
					if ( name == "type" && jQuery.nodeName( elem, "input" ) && elem.parentNode )
						throw "type property can't be changed";

					elem[ name ] = value;
				}

				// browsers index elements by id/name on forms, give priority to attributes.
				if( jQuery.nodeName( elem, "form" ) && elem.getAttributeNode(name) )
					return elem.getAttributeNode( name ).nodeValue;

				// elem.tabIndex doesn't always return the correct value when it hasn't been explicitly set
				// http://fluidproject.org/blog/2008/01/09/getting-setting-and-removing-tabindex-values-with-javascript/
				if ( name == "tabIndex" ) {
					var attributeNode = elem.getAttributeNode( "tabIndex" );
					return attributeNode && attributeNode.specified
						? attributeNode.value
						: elem.nodeName.match(/(button|input|object|select|textarea)/i)
							? 0
							: elem.nodeName.match(/^(a|area)$/i) && elem.href
								? 0
								: undefined;
				}

				return elem[ name ];
			}

			if ( !jQuery.support.style && notxml &&  name == "style" )
				return jQuery.attr( elem.style, "cssText", value );

			if ( set )
				// convert the value to a string (all browsers do this but IE) see #1070
				elem.setAttribute( name, "" + value );

			var attr = !jQuery.support.hrefNormalized && notxml && special
					// Some attributes require a special call on IE
					? elem.getAttribute( name, 2 )
					: elem.getAttribute( name );

			// Non-existent attributes return null, we normalize to undefined
			return attr === null ? undefined : attr;
		}

		// elem is actually elem.style ... set the style

		// IE uses filters for opacity
		if ( !jQuery.support.opacity && name == "opacity" ) {
			if ( set ) {
				// IE has trouble with opacity if it does not have layout
				// Force it by setting the zoom level
				elem.zoom = 1;

				// Set the alpha filter to set the opacity
				elem.filter = (elem.filter || "").replace( /alpha\([^)]*\)/, "" ) +
					(parseInt( value ) + '' == "NaN" ? "" : "alpha(opacity=" + value * 100 + ")");
			}

			return elem.filter && elem.filter.indexOf("opacity=") >= 0 ?
				(parseFloat( elem.filter.match(/opacity=([^)]*)/)[1] ) / 100) + '':
				"";
		}

		name = name.replace(/-([a-z])/ig, function(all, letter){
			return letter.toUpperCase();
		});

		if ( set )
			elem[ name ] = value;

		return elem[ name ];
	},

	trim: function( text ) {
		return (text || "").replace( /^\s+|\s+$/g, "" );
	},

	makeArray: function( array ) {
		var ret = [];

		if( array != null ){
			var i = array.length;
			// The window, strings (and functions) also have 'length'
			if( i == null || typeof array === "string" || jQuery.isFunction(array) || array.setInterval )
				ret[0] = array;
			else
				while( i )
					ret[--i] = array[i];
		}

		return ret;
	},

	inArray: function( elem, array ) {
		for ( var i = 0, length = array.length; i < length; i++ )
		// Use === because on IE, window == document
			if ( array[ i ] === elem )
				return i;

		return -1;
	},

	merge: function( first, second ) {
		// We have to loop this way because IE & Opera overwrite the length
		// expando of getElementsByTagName
		var i = 0, elem, pos = first.length;
		// Also, we need to make sure that the correct elements are being returned
		// (IE returns comment nodes in a '*' query)
		if ( !jQuery.support.getAll ) {
			while ( (elem = second[ i++ ]) != null )
				if ( elem.nodeType != 8 )
					first[ pos++ ] = elem;

		} else
			while ( (elem = second[ i++ ]) != null )
				first[ pos++ ] = elem;

		return first;
	},

	unique: function( array ) {
		var ret = [], done = {};

		try {

			for ( var i = 0, length = array.length; i < length; i++ ) {
				var id = jQuery.data( array[ i ] );

				if ( !done[ id ] ) {
					done[ id ] = true;
					ret.push( array[ i ] );
				}
			}

		} catch( e ) {
			ret = array;
		}

		return ret;
	},

	grep: function( elems, callback, inv ) {
		var ret = [];

		// Go through the array, only saving the items
		// that pass the validator function
		for ( var i = 0, length = elems.length; i < length; i++ )
			if ( !inv != !callback( elems[ i ], i ) )
				ret.push( elems[ i ] );

		return ret;
	},

	map: function( elems, callback ) {
		var ret = [];

		// Go through the array, translating each of the items to their
		// new value (or values).
		for ( var i = 0, length = elems.length; i < length; i++ ) {
			var value = callback( elems[ i ], i );

			if ( value != null )
				ret[ ret.length ] = value;
		}

		return ret.concat.apply( [], ret );
	}
});

// Use of jQuery.browser is deprecated.
// It's included for backwards compatibility and plugins,
// although they should work to migrate away.

var userAgent = navigator.userAgent.toLowerCase();

// Figure out what browser is being used
jQuery.browser = {
	version: (userAgent.match( /.+(?:rv|it|ra|ie)[\/: ]([\d.]+)/ ) || [0,'0'])[1],
	safari: /webkit/.test( userAgent ),
	opera: /opera/.test( userAgent ),
	msie: /msie/.test( userAgent ) && !/opera/.test( userAgent ),
	mozilla: /mozilla/.test( userAgent ) && !/(compatible|webkit)/.test( userAgent )
};

jQuery.each({
	parent: function(elem){return elem.parentNode;},
	parents: function(elem){return jQuery.dir(elem,"parentNode");},
	next: function(elem){return jQuery.nth(elem,2,"nextSibling");},
	prev: function(elem){return jQuery.nth(elem,2,"previousSibling");},
	nextAll: function(elem){return jQuery.dir(elem,"nextSibling");},
	prevAll: function(elem){return jQuery.dir(elem,"previousSibling");},
	siblings: function(elem){return jQuery.sibling(elem.parentNode.firstChild,elem);},
	children: function(elem){return jQuery.sibling(elem.firstChild);},
	contents: function(elem){return jQuery.nodeName(elem,"iframe")?elem.contentDocument||elem.contentWindow.document:jQuery.makeArray(elem.childNodes);}
}, function(name, fn){
	jQuery.fn[ name ] = function( selector ) {
		var ret = jQuery.map( this, fn );

		if ( selector && typeof selector == "string" )
			ret = jQuery.multiFilter( selector, ret );

		return this.pushStack( jQuery.unique( ret ), name, selector );
	};
});

jQuery.each({
	appendTo: "append",
	prependTo: "prepend",
	insertBefore: "before",
	insertAfter: "after",
	replaceAll: "replaceWith"
}, function(name, original){
	jQuery.fn[ name ] = function( selector ) {
		var ret = [], insert = jQuery( selector );

		for ( var i = 0, l = insert.length; i < l; i++ ) {
			var elems = (i > 0 ? this.clone(true) : this).get();
			jQuery.fn[ original ].apply( jQuery(insert[i]), elems );
			ret = ret.concat( elems );
		}

		return this.pushStack( ret, name, selector );
	};
});

jQuery.each({
	removeAttr: function( name ) {
		jQuery.attr( this, name, "" );
		if (this.nodeType == 1)
			this.removeAttribute( name );
	},

	addClass: function( classNames ) {
		jQuery.className.add( this, classNames );
	},

	removeClass: function( classNames ) {
		jQuery.className.remove( this, classNames );
	},

	toggleClass: function( classNames, state ) {
		if( typeof state !== "boolean" )
			state = !jQuery.className.has( this, classNames );
		jQuery.className[ state ? "add" : "remove" ]( this, classNames );
	},

	remove: function( selector ) {
		if ( !selector || jQuery.filter( selector, [ this ] ).length ) {
			// Prevent memory leaks
			jQuery( "*", this ).add([this]).each(function(){
				jQuery.event.remove(this);
				jQuery.removeData(this);
			});
			if (this.parentNode)
				this.parentNode.removeChild( this );
		}
	},

	empty: function() {
		// Remove element nodes and prevent memory leaks
		jQuery(this).children().remove();

		// Remove any remaining nodes
		while ( this.firstChild )
			this.removeChild( this.firstChild );
	}
}, function(name, fn){
	jQuery.fn[ name ] = function(){
		return this.each( fn, arguments );
	};
});

// Helper function used by the dimensions and offset modules
function num(elem, prop) {
	return elem[0] && parseInt( jQuery.curCSS(elem[0], prop, true), 10 ) || 0;
}
var expando = "jQuery" + now(), uuid = 0, windowData = {};

jQuery.extend({
	cache: {},

	data: function( elem, name, data ) {
		elem = elem == window ?
			windowData :
			elem;

		var id = elem[ expando ];

		// Compute a unique ID for the element
		if ( !id )
			id = elem[ expando ] = ++uuid;

		// Only generate the data cache if we're
		// trying to access or manipulate it
		if ( name && !jQuery.cache[ id ] )
			jQuery.cache[ id ] = {};

		// Prevent overriding the named cache with undefined values
		if ( data !== undefined )
			jQuery.cache[ id ][ name ] = data;

		// Return the named cache data, or the ID for the element
		return name ?
			jQuery.cache[ id ][ name ] :
			id;
	},

	removeData: function( elem, name ) {
		elem = elem == window ?
			windowData :
			elem;

		var id = elem[ expando ];

		// If we want to remove a specific section of the element's data
		if ( name ) {
			if ( jQuery.cache[ id ] ) {
				// Remove the section of cache data
				delete jQuery.cache[ id ][ name ];

				// If we've removed all the data, remove the element's cache
				name = "";

				for ( name in jQuery.cache[ id ] )
					break;

				if ( !name )
					jQuery.removeData( elem );
			}

		// Otherwise, we want to remove all of the element's data
		} else {
			// Clean up the element expando
			try {
				delete elem[ expando ];
			} catch(e){
				// IE has trouble directly removing the expando
				// but it's ok with using removeAttribute
				if ( elem.removeAttribute )
					elem.removeAttribute( expando );
			}

			// Completely remove the data cache
			delete jQuery.cache[ id ];
		}
	},
	queue: function( elem, type, data ) {
		if ( elem ){
	
			type = (type || "fx") + "queue";
	
			var q = jQuery.data( elem, type );
	
			if ( !q || jQuery.isArray(data) )
				q = jQuery.data( elem, type, jQuery.makeArray(data) );
			else if( data )
				q.push( data );
	
		}
		return q;
	},

	dequeue: function( elem, type ){
		var queue = jQuery.queue( elem, type ),
			fn = queue.shift();
		
		if( !type || type === "fx" )
			fn = queue[0];
			
		if( fn !== undefined )
			fn.call(elem);
	}
});

jQuery.fn.extend({
	data: function( key, value ){
		var parts = key.split(".");
		parts[1] = parts[1] ? "." + parts[1] : "";

		if ( value === undefined ) {
			var data = this.triggerHandler("getData" + parts[1] + "!", [parts[0]]);

			if ( data === undefined && this.length )
				data = jQuery.data( this[0], key );

			return data === undefined && parts[1] ?
				this.data( parts[0] ) :
				data;
		} else
			return this.trigger("setData" + parts[1] + "!", [parts[0], value]).each(function(){
				jQuery.data( this, key, value );
			});
	},

	removeData: function( key ){
		return this.each(function(){
			jQuery.removeData( this, key );
		});
	},
	queue: function(type, data){
		if ( typeof type !== "string" ) {
			data = type;
			type = "fx";
		}

		if ( data === undefined )
			return jQuery.queue( this[0], type );

		return this.each(function(){
			var queue = jQuery.queue( this, type, data );
			
			 if( type == "fx" && queue.length == 1 )
				queue[0].call(this);
		});
	},
	dequeue: function(type){
		return this.each(function(){
			jQuery.dequeue( this, type );
		});
	}
});/*!
 * Sizzle CSS Selector Engine - v0.9.3
 *  Copyright 2009, The Dojo Foundation
 *  Released under the MIT, BSD, and GPL Licenses.
 *  More information: http://sizzlejs.com/
 */
(function(){

var chunker = /((?:\((?:\([^()]+\)|[^()]+)+\)|\[(?:\[[^[\]]*\]|['"][^'"]*['"]|[^[\]"'"]+)+\]|\\.|[^ >+~,(\[\\]+)+|[>+~])(\s*,\s*)?/g,
	done = 0,
	toString = Object.prototype.toString;

var Sizzle = function(selector, context, results, seed) {
	results = results || [];
	context = context || document;

	if ( context.nodeType !== 1 && context.nodeType !== 9 )
		return [];
	
	if ( !selector || typeof selector !== "string" ) {
		return results;
	}

	var parts = [], m, set, checkSet, check, mode, extra, prune = true;
	
	// Reset the position of the chunker regexp (start from head)
	chunker.lastIndex = 0;
	
	while ( (m = chunker.exec(selector)) !== null ) {
		parts.push( m[1] );
		
		if ( m[2] ) {
			extra = RegExp.rightContext;
			break;
		}
	}

	if ( parts.length > 1 && origPOS.exec( selector ) ) {
		if ( parts.length === 2 && Expr.relative[ parts[0] ] ) {
			set = posProcess( parts[0] + parts[1], context );
		} else {
			set = Expr.relative[ parts[0] ] ?
				[ context ] :
				Sizzle( parts.shift(), context );

			while ( parts.length ) {
				selector = parts.shift();

				if ( Expr.relative[ selector ] )
					selector += parts.shift();

				set = posProcess( selector, set );
			}
		}
	} else {
		var ret = seed ?
			{ expr: parts.pop(), set: makeArray(seed) } :
			Sizzle.find( parts.pop(), parts.length === 1 && context.parentNode ? context.parentNode : context, isXML(context) );
		set = Sizzle.filter( ret.expr, ret.set );

		if ( parts.length > 0 ) {
			checkSet = makeArray(set);
		} else {
			prune = false;
		}

		while ( parts.length ) {
			var cur = parts.pop(), pop = cur;

			if ( !Expr.relative[ cur ] ) {
				cur = "";
			} else {
				pop = parts.pop();
			}

			if ( pop == null ) {
				pop = context;
			}

			Expr.relative[ cur ]( checkSet, pop, isXML(context) );
		}
	}

	if ( !checkSet ) {
		checkSet = set;
	}

	if ( !checkSet ) {
		throw "Syntax error, unrecognized expression: " + (cur || selector);
	}

	if ( toString.call(checkSet) === "[object Array]" ) {
		if ( !prune ) {
			results.push.apply( results, checkSet );
		} else if ( context.nodeType === 1 ) {
			for ( var i = 0; checkSet[i] != null; i++ ) {
				if ( checkSet[i] && (checkSet[i] === true || checkSet[i].nodeType === 1 && contains(context, checkSet[i])) ) {
					results.push( set[i] );
				}
			}
		} else {
			for ( var i = 0; checkSet[i] != null; i++ ) {
				if ( checkSet[i] && checkSet[i].nodeType === 1 ) {
					results.push( set[i] );
				}
			}
		}
	} else {
		makeArray( checkSet, results );
	}

	if ( extra ) {
		Sizzle( extra, context, results, seed );

		if ( sortOrder ) {
			hasDuplicate = false;
			results.sort(sortOrder);

			if ( hasDuplicate ) {
				for ( var i = 1; i < results.length; i++ ) {
					if ( results[i] === results[i-1] ) {
						results.splice(i--, 1);
					}
				}
			}
		}
	}

	return results;
};

Sizzle.matches = function(expr, set){
	return Sizzle(expr, null, null, set);
};

Sizzle.find = function(expr, context, isXML){
	var set, match;

	if ( !expr ) {
		return [];
	}

	for ( var i = 0, l = Expr.order.length; i < l; i++ ) {
		var type = Expr.order[i], match;
		
		if ( (match = Expr.match[ type ].exec( expr )) ) {
			var left = RegExp.leftContext;

			if ( left.substr( left.length - 1 ) !== "\\" ) {
				match[1] = (match[1] || "").replace(/\\/g, "");
				set = Expr.find[ type ]( match, context, isXML );
				if ( set != null ) {
					expr = expr.replace( Expr.match[ type ], "" );
					break;
				}
			}
		}
	}

	if ( !set ) {
		set = context.getElementsByTagName("*");
	}

	return {set: set, expr: expr};
};

Sizzle.filter = function(expr, set, inplace, not){
	var old = expr, result = [], curLoop = set, match, anyFound,
		isXMLFilter = set && set[0] && isXML(set[0]);

	while ( expr && set.length ) {
		for ( var type in Expr.filter ) {
			if ( (match = Expr.match[ type ].exec( expr )) != null ) {
				var filter = Expr.filter[ type ], found, item;
				anyFound = false;

				if ( curLoop == result ) {
					result = [];
				}

				if ( Expr.preFilter[ type ] ) {
					match = Expr.preFilter[ type ]( match, curLoop, inplace, result, not, isXMLFilter );

					if ( !match ) {
						anyFound = found = true;
					} else if ( match === true ) {
						continue;
					}
				}

				if ( match ) {
					for ( var i = 0; (item = curLoop[i]) != null; i++ ) {
						if ( item ) {
							found = filter( item, match, i, curLoop );
							var pass = not ^ !!found;

							if ( inplace && found != null ) {
								if ( pass ) {
									anyFound = true;
								} else {
									curLoop[i] = false;
								}
							} else if ( pass ) {
								result.push( item );
								anyFound = true;
							}
						}
					}
				}

				if ( found !== undefined ) {
					if ( !inplace ) {
						curLoop = result;
					}

					expr = expr.replace( Expr.match[ type ], "" );

					if ( !anyFound ) {
						return [];
					}

					break;
				}
			}
		}

		// Improper expression
		if ( expr == old ) {
			if ( anyFound == null ) {
				throw "Syntax error, unrecognized expression: " + expr;
			} else {
				break;
			}
		}

		old = expr;
	}

	return curLoop;
};

var Expr = Sizzle.selectors = {
	order: [ "ID", "NAME", "TAG" ],
	match: {
		ID: /#((?:[\w\u00c0-\uFFFF_-]|\\.)+)/,
		CLASS: /\.((?:[\w\u00c0-\uFFFF_-]|\\.)+)/,
		NAME: /\[name=['"]*((?:[\w\u00c0-\uFFFF_-]|\\.)+)['"]*\]/,
		ATTR: /\[\s*((?:[\w\u00c0-\uFFFF_-]|\\.)+)\s*(?:(\S?=)\s*(['"]*)(.*?)\3|)\s*\]/,
		TAG: /^((?:[\w\u00c0-\uFFFF\*_-]|\\.)+)/,
		CHILD: /:(only|nth|last|first)-child(?:\((even|odd|[\dn+-]*)\))?/,
		POS: /:(nth|eq|gt|lt|first|last|even|odd)(?:\((\d*)\))?(?=[^-]|$)/,
		PSEUDO: /:((?:[\w\u00c0-\uFFFF_-]|\\.)+)(?:\((['"]*)((?:\([^\)]+\)|[^\2\(\)]*)+)\2\))?/
	},
	attrMap: {
		"class": "className",
		"for": "htmlFor"
	},
	attrHandle: {
		href: function(elem){
			return elem.getAttribute("href");
		}
	},
	relative: {
		"+": function(checkSet, part, isXML){
			var isPartStr = typeof part === "string",
				isTag = isPartStr && !/\W/.test(part),
				isPartStrNotTag = isPartStr && !isTag;

			if ( isTag && !isXML ) {
				part = part.toUpperCase();
			}

			for ( var i = 0, l = checkSet.length, elem; i < l; i++ ) {
				if ( (elem = checkSet[i]) ) {
					while ( (elem = elem.previousSibling) && elem.nodeType !== 1 ) {}

					checkSet[i] = isPartStrNotTag || elem && elem.nodeName === part ?
						elem || false :
						elem === part;
				}
			}

			if ( isPartStrNotTag ) {
				Sizzle.filter( part, checkSet, true );
			}
		},
		">": function(checkSet, part, isXML){
			var isPartStr = typeof part === "string";

			if ( isPartStr && !/\W/.test(part) ) {
				part = isXML ? part : part.toUpperCase();

				for ( var i = 0, l = checkSet.length; i < l; i++ ) {
					var elem = checkSet[i];
					if ( elem ) {
						var parent = elem.parentNode;
						checkSet[i] = parent.nodeName === part ? parent : false;
					}
				}
			} else {
				for ( var i = 0, l = checkSet.length; i < l; i++ ) {
					var elem = checkSet[i];
					if ( elem ) {
						checkSet[i] = isPartStr ?
							elem.parentNode :
							elem.parentNode === part;
					}
				}

				if ( isPartStr ) {
					Sizzle.filter( part, checkSet, true );
				}
			}
		},
		"": function(checkSet, part, isXML){
			var doneName = done++, checkFn = dirCheck;

			if ( !part.match(/\W/) ) {
				var nodeCheck = part = isXML ? part : part.toUpperCase();
				checkFn = dirNodeCheck;
			}

			checkFn("parentNode", part, doneName, checkSet, nodeCheck, isXML);
		},
		"~": function(checkSet, part, isXML){
			var doneName = done++, checkFn = dirCheck;

			if ( typeof part === "string" && !part.match(/\W/) ) {
				var nodeCheck = part = isXML ? part : part.toUpperCase();
				checkFn = dirNodeCheck;
			}

			checkFn("previousSibling", part, doneName, checkSet, nodeCheck, isXML);
		}
	},
	find: {
		ID: function(match, context, isXML){
			if ( typeof context.getElementById !== "undefined" && !isXML ) {
				var m = context.getElementById(match[1]);
				return m ? [m] : [];
			}
		},
		NAME: function(match, context, isXML){
			if ( typeof context.getElementsByName !== "undefined" ) {
				var ret = [], results = context.getElementsByName(match[1]);

				for ( var i = 0, l = results.length; i < l; i++ ) {
					if ( results[i].getAttribute("name") === match[1] ) {
						ret.push( results[i] );
					}
				}

				return ret.length === 0 ? null : ret;
			}
		},
		TAG: function(match, context){
			return context.getElementsByTagName(match[1]);
		}
	},
	preFilter: {
		CLASS: function(match, curLoop, inplace, result, not, isXML){
			match = " " + match[1].replace(/\\/g, "") + " ";

			if ( isXML ) {
				return match;
			}

			for ( var i = 0, elem; (elem = curLoop[i]) != null; i++ ) {
				if ( elem ) {
					if ( not ^ (elem.className && (" " + elem.className + " ").indexOf(match) >= 0) ) {
						if ( !inplace )
							result.push( elem );
					} else if ( inplace ) {
						curLoop[i] = false;
					}
				}
			}

			return false;
		},
		ID: function(match){
			return match[1].replace(/\\/g, "");
		},
		TAG: function(match, curLoop){
			for ( var i = 0; curLoop[i] === false; i++ ){}
			return curLoop[i] && isXML(curLoop[i]) ? match[1] : match[1].toUpperCase();
		},
		CHILD: function(match){
			if ( match[1] == "nth" ) {
				// parse equations like 'even', 'odd', '5', '2n', '3n+2', '4n-1', '-n+6'
				var test = /(-?)(\d*)n((?:\+|-)?\d*)/.exec(
					match[2] == "even" && "2n" || match[2] == "odd" && "2n+1" ||
					!/\D/.test( match[2] ) && "0n+" + match[2] || match[2]);

				// calculate the numbers (first)n+(last) including if they are negative
				match[2] = (test[1] + (test[2] || 1)) - 0;
				match[3] = test[3] - 0;
			}

			// TODO: Move to normal caching system
			match[0] = done++;

			return match;
		},
		ATTR: function(match, curLoop, inplace, result, not, isXML){
			var name = match[1].replace(/\\/g, "");
			
			if ( !isXML && Expr.attrMap[name] ) {
				match[1] = Expr.attrMap[name];
			}

			if ( match[2] === "~=" ) {
				match[4] = " " + match[4] + " ";
			}

			return match;
		},
		PSEUDO: function(match, curLoop, inplace, result, not){
			if ( match[1] === "not" ) {
				// If we're dealing with a complex expression, or a simple one
				if ( match[3].match(chunker).length > 1 || /^\w/.test(match[3]) ) {
					match[3] = Sizzle(match[3], null, null, curLoop);
				} else {
					var ret = Sizzle.filter(match[3], curLoop, inplace, true ^ not);
					if ( !inplace ) {
						result.push.apply( result, ret );
					}
					return false;
				}
			} else if ( Expr.match.POS.test( match[0] ) || Expr.match.CHILD.test( match[0] ) ) {
				return true;
			}
			
			return match;
		},
		POS: function(match){
			match.unshift( true );
			return match;
		}
	},
	filters: {
		enabled: function(elem){
			return elem.disabled === false && elem.type !== "hidden";
		},
		disabled: function(elem){
			return elem.disabled === true;
		},
		checked: function(elem){
			return elem.checked === true;
		},
		selected: function(elem){
			// Accessing this property makes selected-by-default
			// options in Safari work properly
			elem.parentNode.selectedIndex;
			return elem.selected === true;
		},
		parent: function(elem){
			return !!elem.firstChild;
		},
		empty: function(elem){
			return !elem.firstChild;
		},
		has: function(elem, i, match){
			return !!Sizzle( match[3], elem ).length;
		},
		header: function(elem){
			return /h\d/i.test( elem.nodeName );
		},
		text: function(elem){
			return "text" === elem.type;
		},
		radio: function(elem){
			return "radio" === elem.type;
		},
		checkbox: function(elem){
			return "checkbox" === elem.type;
		},
		file: function(elem){
			return "file" === elem.type;
		},
		password: function(elem){
			return "password" === elem.type;
		},
		submit: function(elem){
			return "submit" === elem.type;
		},
		image: function(elem){
			return "image" === elem.type;
		},
		reset: function(elem){
			return "reset" === elem.type;
		},
		button: function(elem){
			return "button" === elem.type || elem.nodeName.toUpperCase() === "BUTTON";
		},
		input: function(elem){
			return /input|select|textarea|button/i.test(elem.nodeName);
		}
	},
	setFilters: {
		first: function(elem, i){
			return i === 0;
		},
		last: function(elem, i, match, array){
			return i === array.length - 1;
		},
		even: function(elem, i){
			return i % 2 === 0;
		},
		odd: function(elem, i){
			return i % 2 === 1;
		},
		lt: function(elem, i, match){
			return i < match[3] - 0;
		},
		gt: function(elem, i, match){
			return i > match[3] - 0;
		},
		nth: function(elem, i, match){
			return match[3] - 0 == i;
		},
		eq: function(elem, i, match){
			return match[3] - 0 == i;
		}
	},
	filter: {
		PSEUDO: function(elem, match, i, array){
			var name = match[1], filter = Expr.filters[ name ];

			if ( filter ) {
				return filter( elem, i, match, array );
			} else if ( name === "contains" ) {
				return (elem.textContent || elem.innerText || "").indexOf(match[3]) >= 0;
			} else if ( name === "not" ) {
				var not = match[3];

				for ( var i = 0, l = not.length; i < l; i++ ) {
					if ( not[i] === elem ) {
						return false;
					}
				}

				return true;
			}
		},
		CHILD: function(elem, match){
			var type = match[1], node = elem;
			switch (type) {
				case 'only':
				case 'first':
					while (node = node.previousSibling)  {
						if ( node.nodeType === 1 ) return false;
					}
					if ( type == 'first') return true;
					node = elem;
				case 'last':
					while (node = node.nextSibling)  {
						if ( node.nodeType === 1 ) return false;
					}
					return true;
				case 'nth':
					var first = match[2], last = match[3];

					if ( first == 1 && last == 0 ) {
						return true;
					}
					
					var doneName = match[0],
						parent = elem.parentNode;
	
					if ( parent && (parent.sizcache !== doneName || !elem.nodeIndex) ) {
						var count = 0;
						for ( node = parent.firstChild; node; node = node.nextSibling ) {
							if ( node.nodeType === 1 ) {
								node.nodeIndex = ++count;
							}
						} 
						parent.sizcache = doneName;
					}
					
					var diff = elem.nodeIndex - last;
					if ( first == 0 ) {
						return diff == 0;
					} else {
						return ( diff % first == 0 && diff / first >= 0 );
					}
			}
		},
		ID: function(elem, match){
			return elem.nodeType === 1 && elem.getAttribute("id") === match;
		},
		TAG: function(elem, match){
			return (match === "*" && elem.nodeType === 1) || elem.nodeName === match;
		},
		CLASS: function(elem, match){
			return (" " + (elem.className || elem.getAttribute("class")) + " ")
				.indexOf( match ) > -1;
		},
		ATTR: function(elem, match){
			var name = match[1],
				result = Expr.attrHandle[ name ] ?
					Expr.attrHandle[ name ]( elem ) :
					elem[ name ] != null ?
						elem[ name ] :
						elem.getAttribute( name ),
				value = result + "",
				type = match[2],
				check = match[4];

			return result == null ?
				type === "!=" :
				type === "=" ?
				value === check :
				type === "*=" ?
				value.indexOf(check) >= 0 :
				type === "~=" ?
				(" " + value + " ").indexOf(check) >= 0 :
				!check ?
				value && result !== false :
				type === "!=" ?
				value != check :
				type === "^=" ?
				value.indexOf(check) === 0 :
				type === "$=" ?
				value.substr(value.length - check.length) === check :
				type === "|=" ?
				value === check || value.substr(0, check.length + 1) === check + "-" :
				false;
		},
		POS: function(elem, match, i, array){
			var name = match[2], filter = Expr.setFilters[ name ];

			if ( filter ) {
				return filter( elem, i, match, array );
			}
		}
	}
};

var origPOS = Expr.match.POS;

for ( var type in Expr.match ) {
	Expr.match[ type ] = RegExp( Expr.match[ type ].source + /(?![^\[]*\])(?![^\(]*\))/.source );
}

var makeArray = function(array, results) {
	array = Array.prototype.slice.call( array );

	if ( results ) {
		results.push.apply( results, array );
		return results;
	}
	
	return array;
};

// Perform a simple check to determine if the browser is capable of
// converting a NodeList to an array using builtin methods.
try {
	Array.prototype.slice.call( document.documentElement.childNodes );

// Provide a fallback method if it does not work
} catch(e){
	makeArray = function(array, results) {
		var ret = results || [];

		if ( toString.call(array) === "[object Array]" ) {
			Array.prototype.push.apply( ret, array );
		} else {
			if ( typeof array.length === "number" ) {
				for ( var i = 0, l = array.length; i < l; i++ ) {
					ret.push( array[i] );
				}
			} else {
				for ( var i = 0; array[i]; i++ ) {
					ret.push( array[i] );
				}
			}
		}

		return ret;
	};
}

var sortOrder;

if ( document.documentElement.compareDocumentPosition ) {
	sortOrder = function( a, b ) {
		var ret = a.compareDocumentPosition(b) & 4 ? -1 : a === b ? 0 : 1;
		if ( ret === 0 ) {
			hasDuplicate = true;
		}
		return ret;
	};
} else if ( "sourceIndex" in document.documentElement ) {
	sortOrder = function( a, b ) {
		var ret = a.sourceIndex - b.sourceIndex;
		if ( ret === 0 ) {
			hasDuplicate = true;
		}
		return ret;
	};
} else if ( document.createRange ) {
	sortOrder = function( a, b ) {
		var aRange = a.ownerDocument.createRange(), bRange = b.ownerDocument.createRange();
		aRange.selectNode(a);
		aRange.collapse(true);
		bRange.selectNode(b);
		bRange.collapse(true);
		var ret = aRange.compareBoundaryPoints(Range.START_TO_END, bRange);
		if ( ret === 0 ) {
			hasDuplicate = true;
		}
		return ret;
	};
}

// Check to see if the browser returns elements by name when
// querying by getElementById (and provide a workaround)
(function(){
	// We're going to inject a fake input element with a specified name
	var form = document.createElement("form"),
		id = "script" + (new Date).getTime();
	form.innerHTML = "<input name='" + id + "'/>";

	// Inject it into the root element, check its status, and remove it quickly
	var root = document.documentElement;
	root.insertBefore( form, root.firstChild );

	// The workaround has to do additional checks after a getElementById
	// Which slows things down for other browsers (hence the branching)
	if ( !!document.getElementById( id ) ) {
		Expr.find.ID = function(match, context, isXML){
			if ( typeof context.getElementById !== "undefined" && !isXML ) {
				var m = context.getElementById(match[1]);
				return m ? m.id === match[1] || typeof m.getAttributeNode !== "undefined" && m.getAttributeNode("id").nodeValue === match[1] ? [m] : undefined : [];
			}
		};

		Expr.filter.ID = function(elem, match){
			var node = typeof elem.getAttributeNode !== "undefined" && elem.getAttributeNode("id");
			return elem.nodeType === 1 && node && node.nodeValue === match;
		};
	}

	root.removeChild( form );
})();

(function(){
	// Check to see if the browser returns only elements
	// when doing getElementsByTagName("*")

	// Create a fake element
	var div = document.createElement("div");
	div.appendChild( document.createComment("") );

	// Make sure no comments are found
	if ( div.getElementsByTagName("*").length > 0 ) {
		Expr.find.TAG = function(match, context){
			var results = context.getElementsByTagName(match[1]);

			// Filter out possible comments
			if ( match[1] === "*" ) {
				var tmp = [];

				for ( var i = 0; results[i]; i++ ) {
					if ( results[i].nodeType === 1 ) {
						tmp.push( results[i] );
					}
				}

				results = tmp;
			}

			return results;
		};
	}

	// Check to see if an attribute returns normalized href attributes
	div.innerHTML = "<a href='#'></a>";
	if ( div.firstChild && typeof div.firstChild.getAttribute !== "undefined" &&
			div.firstChild.getAttribute("href") !== "#" ) {
		Expr.attrHandle.href = function(elem){
			return elem.getAttribute("href", 2);
		};
	}
})();

if ( document.querySelectorAll ) (function(){
	var oldSizzle = Sizzle, div = document.createElement("div");
	div.innerHTML = "<p class='TEST'></p>";

	// Safari can't handle uppercase or unicode characters when
	// in quirks mode.
	if ( div.querySelectorAll && div.querySelectorAll(".TEST").length === 0 ) {
		return;
	}
	
	Sizzle = function(query, context, extra, seed){
		context = context || document;

		// Only use querySelectorAll on non-XML documents
		// (ID selectors don't work in non-HTML documents)
		if ( !seed && context.nodeType === 9 && !isXML(context) ) {
			try {
				return makeArray( context.querySelectorAll(query), extra );
			} catch(e){}
		}
		
		return oldSizzle(query, context, extra, seed);
	};

	Sizzle.find = oldSizzle.find;
	Sizzle.filter = oldSizzle.filter;
	Sizzle.selectors = oldSizzle.selectors;
	Sizzle.matches = oldSizzle.matches;
})();

if ( document.getElementsByClassName && document.documentElement.getElementsByClassName ) (function(){
	var div = document.createElement("div");
	div.innerHTML = "<div class='test e'></div><div class='test'></div>";

	// Opera can't find a second classname (in 9.6)
	if ( div.getElementsByClassName("e").length === 0 )
		return;

	// Safari caches class attributes, doesn't catch changes (in 3.2)
	div.lastChild.className = "e";

	if ( div.getElementsByClassName("e").length === 1 )
		return;

	Expr.order.splice(1, 0, "CLASS");
	Expr.find.CLASS = function(match, context, isXML) {
		if ( typeof context.getElementsByClassName !== "undefined" && !isXML ) {
			return context.getElementsByClassName(match[1]);
		}
	};
})();

function dirNodeCheck( dir, cur, doneName, checkSet, nodeCheck, isXML ) {
	var sibDir = dir == "previousSibling" && !isXML;
	for ( var i = 0, l = checkSet.length; i < l; i++ ) {
		var elem = checkSet[i];
		if ( elem ) {
			if ( sibDir && elem.nodeType === 1 ){
				elem.sizcache = doneName;
				elem.sizset = i;
			}
			elem = elem[dir];
			var match = false;

			while ( elem ) {
				if ( elem.sizcache === doneName ) {
					match = checkSet[elem.sizset];
					break;
				}

				if ( elem.nodeType === 1 && !isXML ){
					elem.sizcache = doneName;
					elem.sizset = i;
				}

				if ( elem.nodeName === cur ) {
					match = elem;
					break;
				}

				elem = elem[dir];
			}

			checkSet[i] = match;
		}
	}
}

function dirCheck( dir, cur, doneName, checkSet, nodeCheck, isXML ) {
	var sibDir = dir == "previousSibling" && !isXML;
	for ( var i = 0, l = checkSet.length; i < l; i++ ) {
		var elem = checkSet[i];
		if ( elem ) {
			if ( sibDir && elem.nodeType === 1 ) {
				elem.sizcache = doneName;
				elem.sizset = i;
			}
			elem = elem[dir];
			var match = false;

			while ( elem ) {
				if ( elem.sizcache === doneName ) {
					match = checkSet[elem.sizset];
					break;
				}

				if ( elem.nodeType === 1 ) {
					if ( !isXML ) {
						elem.sizcache = doneName;
						elem.sizset = i;
					}
					if ( typeof cur !== "string" ) {
						if ( elem === cur ) {
							match = true;
							break;
						}

					} else if ( Sizzle.filter( cur, [elem] ).length > 0 ) {
						match = elem;
						break;
					}
				}

				elem = elem[dir];
			}

			checkSet[i] = match;
		}
	}
}

var contains = document.compareDocumentPosition ?  function(a, b){
	return a.compareDocumentPosition(b) & 16;
} : function(a, b){
	return a !== b && (a.contains ? a.contains(b) : true);
};

var isXML = function(elem){
	return elem.nodeType === 9 && elem.documentElement.nodeName !== "HTML" ||
		!!elem.ownerDocument && isXML( elem.ownerDocument );
};

var posProcess = function(selector, context){
	var tmpSet = [], later = "", match,
		root = context.nodeType ? [context] : context;

	// Position selectors must be done after the filter
	// And so must :not(positional) so we move all PSEUDOs to the end
	while ( (match = Expr.match.PSEUDO.exec( selector )) ) {
		later += match[0];
		selector = selector.replace( Expr.match.PSEUDO, "" );
	}

	selector = Expr.relative[selector] ? selector + "*" : selector;

	for ( var i = 0, l = root.length; i < l; i++ ) {
		Sizzle( selector, root[i], tmpSet );
	}

	return Sizzle.filter( later, tmpSet );
};

// EXPOSE
jQuery.find = Sizzle;
jQuery.filter = Sizzle.filter;
jQuery.expr = Sizzle.selectors;
jQuery.expr[":"] = jQuery.expr.filters;

Sizzle.selectors.filters.hidden = function(elem){
	return elem.offsetWidth === 0 || elem.offsetHeight === 0;
};

Sizzle.selectors.filters.visible = function(elem){
	return elem.offsetWidth > 0 || elem.offsetHeight > 0;
};

Sizzle.selectors.filters.animated = function(elem){
	return jQuery.grep(jQuery.timers, function(fn){
		return elem === fn.elem;
	}).length;
};

jQuery.multiFilter = function( expr, elems, not ) {
	if ( not ) {
		expr = ":not(" + expr + ")";
	}

	return Sizzle.matches(expr, elems);
};

jQuery.dir = function( elem, dir ){
	var matched = [], cur = elem[dir];
	while ( cur && cur != document ) {
		if ( cur.nodeType == 1 )
			matched.push( cur );
		cur = cur[dir];
	}
	return matched;
};

jQuery.nth = function(cur, result, dir, elem){
	result = result || 1;
	var num = 0;

	for ( ; cur; cur = cur[dir] )
		if ( cur.nodeType == 1 && ++num == result )
			break;

	return cur;
};

jQuery.sibling = function(n, elem){
	var r = [];

	for ( ; n; n = n.nextSibling ) {
		if ( n.nodeType == 1 && n != elem )
			r.push( n );
	}

	return r;
};

return;

window.Sizzle = Sizzle;

})();
/*
 * A number of helper functions used for managing events.
 * Many of the ideas behind this code originated from
 * Dean Edwards' addEvent library.
 */
jQuery.event = {

	// Bind an event to an element
	// Original by Dean Edwards
	add: function(elem, types, handler, data) {
		if ( elem.nodeType == 3 || elem.nodeType == 8 )
			return;

		// For whatever reason, IE has trouble passing the window object
		// around, causing it to be cloned in the process
		if ( elem.setInterval && elem != window )
			elem = window;

		// Make sure that the function being executed has a unique ID
		if ( !handler.guid )
			handler.guid = this.guid++;

		// if data is passed, bind to handler
		if ( data !== undefined ) {
			// Create temporary function pointer to original handler
			var fn = handler;

			// Create unique handler function, wrapped around original handler
			handler = this.proxy( fn );

			// Store data in unique handler
			handler.data = data;
		}

		// Init the element's event structure
		var events = jQuery.data(elem, "events") || jQuery.data(elem, "events", {}),
			handle = jQuery.data(elem, "handle") || jQuery.data(elem, "handle", function(){
				// Handle the second event of a trigger and when
				// an event is called after a page has unloaded
				return typeof jQuery !== "undefined" && !jQuery.event.triggered ?
					jQuery.event.handle.apply(arguments.callee.elem, arguments) :
					undefined;
			});
		// Add elem as a property of the handle function
		// This is to prevent a memory leak with non-native
		// event in IE.
		handle.elem = elem;

		// Handle multiple events separated by a space
		// jQuery(...).bind("mouseover mouseout", fn);
		jQuery.each(types.split(/\s+/), function(index, type) {
			// Namespaced event handlers
			var namespaces = type.split(".");
			type = namespaces.shift();
			handler.type = namespaces.slice().sort().join(".");

			// Get the current list of functions bound to this event
			var handlers = events[type];
			
			if ( jQuery.event.specialAll[type] )
				jQuery.event.specialAll[type].setup.call(elem, data, namespaces);

			// Init the event handler queue
			if (!handlers) {
				handlers = events[type] = {};

				// Check for a special event handler
				// Only use addEventListener/attachEvent if the special
				// events handler returns false
				if ( !jQuery.event.special[type] || jQuery.event.special[type].setup.call(elem, data, namespaces) === false ) {
					// Bind the global event handler to the element
					if (elem.addEventListener)
						elem.addEventListener(type, handle, false);
					else if (elem.attachEvent)
						elem.attachEvent("on" + type, handle);
				}
			}

			// Add the function to the element's handler list
			handlers[handler.guid] = handler;

			// Keep track of which events have been used, for global triggering
			jQuery.event.global[type] = true;
		});

		// Nullify elem to prevent memory leaks in IE
		elem = null;
	},

	guid: 1,
	global: {},

	// Detach an event or set of events from an element
	remove: function(elem, types, handler) {
		// don't do events on text and comment nodes
		if ( elem.nodeType == 3 || elem.nodeType == 8 )
			return;

		var events = jQuery.data(elem, "events"), ret, index;

		if ( events ) {
			// Unbind all events for the element
			if ( types === undefined || (typeof types === "string" && types.charAt(0) == ".") )
				for ( var type in events )
					this.remove( elem, type + (types || "") );
			else {
				// types is actually an event object here
				if ( types.type ) {
					handler = types.handler;
					types = types.type;
				}

				// Handle multiple events seperated by a space
				// jQuery(...).unbind("mouseover mouseout", fn);
				jQuery.each(types.split(/\s+/), function(index, type){
					// Namespaced event handlers
					var namespaces = type.split(".");
					type = namespaces.shift();
					var namespace = RegExp("(^|\\.)" + namespaces.slice().sort().join(".*\\.") + "(\\.|$)");

					if ( events[type] ) {
						// remove the given handler for the given type
						if ( handler )
							delete events[type][handler.guid];

						// remove all handlers for the given type
						else
							for ( var handle in events[type] )
								// Handle the removal of namespaced events
								if ( namespace.test(events[type][handle].type) )
									delete events[type][handle];
									
						if ( jQuery.event.specialAll[type] )
							jQuery.event.specialAll[type].teardown.call(elem, namespaces);

						// remove generic event handler if no more handlers exist
						for ( ret in events[type] ) break;
						if ( !ret ) {
							if ( !jQuery.event.special[type] || jQuery.event.special[type].teardown.call(elem, namespaces) === false ) {
								if (elem.removeEventListener)
									elem.removeEventListener(type, jQuery.data(elem, "handle"), false);
								else if (elem.detachEvent)
									elem.detachEvent("on" + type, jQuery.data(elem, "handle"));
							}
							ret = null;
							delete events[type];
						}
					}
				});
			}

			// Remove the expando if it's no longer used
			for ( ret in events ) break;
			if ( !ret ) {
				var handle = jQuery.data( elem, "handle" );
				if ( handle ) handle.elem = null;
				jQuery.removeData( elem, "events" );
				jQuery.removeData( elem, "handle" );
			}
		}
	},

	// bubbling is internal
	trigger: function( event, data, elem, bubbling ) {
		// Event object or event type
		var type = event.type || event;

		if( !bubbling ){
			event = typeof event === "object" ?
				// jQuery.Event object
				event[expando] ? event :
				// Object literal
				jQuery.extend( jQuery.Event(type), event ) :
				// Just the event type (string)
				jQuery.Event(type);

			if ( type.indexOf("!") >= 0 ) {
				event.type = type = type.slice(0, -1);
				event.exclusive = true;
			}

			// Handle a global trigger
			if ( !elem ) {
				// Don't bubble custom events when global (to avoid too much overhead)
				event.stopPropagation();
				// Only trigger if we've ever bound an event for it
				if ( this.global[type] )
					jQuery.each( jQuery.cache, function(){
						if ( this.events && this.events[type] )
							jQuery.event.trigger( event, data, this.handle.elem );
					});
			}

			// Handle triggering a single element

			// don't do events on text and comment nodes
			if ( !elem || elem.nodeType == 3 || elem.nodeType == 8 )
				return undefined;
			
			// Clean up in case it is reused
			event.result = undefined;
			event.target = elem;
			
			// Clone the incoming data, if any
			data = jQuery.makeArray(data);
			data.unshift( event );
		}

		event.currentTarget = elem;

		// Trigger the event, it is assumed that "handle" is a function
		var handle = jQuery.data(elem, "handle");
		if ( handle )
			handle.apply( elem, data );

		// Handle triggering native .onfoo handlers (and on links since we don't call .click() for links)
		if ( (!elem[type] || (jQuery.nodeName(elem, 'a') && type == "click")) && elem["on"+type] && elem["on"+type].apply( elem, data ) === false )
			event.result = false;

		// Trigger the native events (except for clicks on links)
		if ( !bubbling && elem[type] && !event.isDefaultPrevented() && !(jQuery.nodeName(elem, 'a') && type == "click") ) {
			this.triggered = true;
			try {
				elem[ type ]();
			// prevent IE from throwing an error for some hidden elements
			} catch (e) {}
		}

		this.triggered = false;

		if ( !event.isPropagationStopped() ) {
			var parent = elem.parentNode || elem.ownerDocument;
			if ( parent )
				jQuery.event.trigger(event, data, parent, true);
		}
	},

	handle: function(event) {
		// returned undefined or false
		var all, handlers;

		event = arguments[0] = jQuery.event.fix( event || window.event );
		event.currentTarget = this;
		
		// Namespaced event handlers
		var namespaces = event.type.split(".");
		event.type = namespaces.shift();

		// Cache this now, all = true means, any handler
		all = !namespaces.length && !event.exclusive;
		
		var namespace = RegExp("(^|\\.)" + namespaces.slice().sort().join(".*\\.") + "(\\.|$)");

		handlers = ( jQuery.data(this, "events") || {} )[event.type];

		for ( var j in handlers ) {
			var handler = handlers[j];

			// Filter the functions by class
			if ( all || namespace.test(handler.type) ) {
				// Pass in a reference to the handler function itself
				// So that we can later remove it
				event.handler = handler;
				event.data = handler.data;

				var ret = handler.apply(this, arguments);

				if( ret !== undefined ){
					event.result = ret;
					if ( ret === false ) {
						event.preventDefault();
						event.stopPropagation();
					}
				}

				if( event.isImmediatePropagationStopped() )
					break;

			}
		}
	},

	props: "altKey attrChange attrName bubbles button cancelable charCode clientX clientY ctrlKey currentTarget data detail eventPhase fromElement handler keyCode metaKey newValue originalTarget pageX pageY prevValue relatedNode relatedTarget screenX screenY shiftKey srcElement target toElement view wheelDelta which".split(" "),

	fix: function(event) {
		if ( event[expando] )
			return event;

		// store a copy of the original event object
		// and "clone" to set read-only properties
		var originalEvent = event;
		event = jQuery.Event( originalEvent );

		for ( var i = this.props.length, prop; i; ){
			prop = this.props[ --i ];
			event[ prop ] = originalEvent[ prop ];
		}

		// Fix target property, if necessary
		if ( !event.target )
			event.target = event.srcElement || document; // Fixes #1925 where srcElement might not be defined either

		// check if target is a textnode (safari)
		if ( event.target.nodeType == 3 )
			event.target = event.target.parentNode;

		// Add relatedTarget, if necessary
		if ( !event.relatedTarget && event.fromElement )
			event.relatedTarget = event.fromElement == event.target ? event.toElement : event.fromElement;

		// Calculate pageX/Y if missing and clientX/Y available
		if ( event.pageX == null && event.clientX != null ) {
			var doc = document.documentElement, body = document.body;
			event.pageX = event.clientX + (doc && doc.scrollLeft || body && body.scrollLeft || 0) - (doc.clientLeft || 0);
			event.pageY = event.clientY + (doc && doc.scrollTop || body && body.scrollTop || 0) - (doc.clientTop || 0);
		}

		// Add which for key events
		if ( !event.which && ((event.charCode || event.charCode === 0) ? event.charCode : event.keyCode) )
			event.which = event.charCode || event.keyCode;

		// Add metaKey to non-Mac browsers (use ctrl for PC's and Meta for Macs)
		if ( !event.metaKey && event.ctrlKey )
			event.metaKey = event.ctrlKey;

		// Add which for click: 1 == left; 2 == middle; 3 == right
		// Note: button is not normalized, so don't use it
		if ( !event.which && event.button )
			event.which = (event.button & 1 ? 1 : ( event.button & 2 ? 3 : ( event.button & 4 ? 2 : 0 ) ));

		return event;
	},

	proxy: function( fn, proxy ){
		proxy = proxy || function(){ return fn.apply(this, arguments); };
		// Set the guid of unique handler to the same of original handler, so it can be removed
		proxy.guid = fn.guid = fn.guid || proxy.guid || this.guid++;
		// So proxy can be declared as an argument
		return proxy;
	},

	special: {
		ready: {
			// Make sure the ready event is setup
			setup: bindReady,
			teardown: function() {}
		}
	},
	
	specialAll: {
		live: {
			setup: function( selector, namespaces ){
				jQuery.event.add( this, namespaces[0], liveHandler );
			},
			teardown:  function( namespaces ){
				if ( namespaces.length ) {
					var remove = 0, name = RegExp("(^|\\.)" + namespaces[0] + "(\\.|$)");
					
					jQuery.each( (jQuery.data(this, "events").live || {}), function(){
						if ( name.test(this.type) )
							remove++;
					});
					
					if ( remove < 1 )
						jQuery.event.remove( this, namespaces[0], liveHandler );
				}
			}
		}
	}
};

jQuery.Event = function( src ){
	// Allow instantiation without the 'new' keyword
	if( !this.preventDefault )
		return new jQuery.Event(src);
	
	// Event object
	if( src && src.type ){
		this.originalEvent = src;
		this.type = src.type;
	// Event type
	}else
		this.type = src;

	// timeStamp is buggy for some events on Firefox(#3843)
	// So we won't rely on the native value
	this.timeStamp = now();
	
	// Mark it as fixed
	this[expando] = true;
};

function returnFalse(){
	return false;
}
function returnTrue(){
	return true;
}

// jQuery.Event is based on DOM3 Events as specified by the ECMAScript Language Binding
// http://www.w3.org/TR/2003/WD-DOM-Level-3-Events-20030331/ecma-script-binding.html
jQuery.Event.prototype = {
	preventDefault: function() {
		this.isDefaultPrevented = returnTrue;

		var e = this.originalEvent;
		if( !e )
			return;
		// if preventDefault exists run it on the original event
		if (e.preventDefault)
			e.preventDefault();
		// otherwise set the returnValue property of the original event to false (IE)
		e.returnValue = false;
	},
	stopPropagation: function() {
		this.isPropagationStopped = returnTrue;

		var e = this.originalEvent;
		if( !e )
			return;
		// if stopPropagation exists run it on the original event
		if (e.stopPropagation)
			e.stopPropagation();
		// otherwise set the cancelBubble property of the original event to true (IE)
		e.cancelBubble = true;
	},
	stopImmediatePropagation:function(){
		this.isImmediatePropagationStopped = returnTrue;
		this.stopPropagation();
	},
	isDefaultPrevented: returnFalse,
	isPropagationStopped: returnFalse,
	isImmediatePropagationStopped: returnFalse
};
// Checks if an event happened on an element within another element
// Used in jQuery.event.special.mouseenter and mouseleave handlers
var withinElement = function(event) {
	// Check if mouse(over|out) are still within the same parent element
	var parent = event.relatedTarget;
	// Traverse up the tree
	while ( parent && parent != this )
		try { parent = parent.parentNode; }
		catch(e) { parent = this; }
	
	if( parent != this ){
		// set the correct event type
		event.type = event.data;
		// handle event if we actually just moused on to a non sub-element
		jQuery.event.handle.apply( this, arguments );
	}
};
	
jQuery.each({ 
	mouseover: 'mouseenter', 
	mouseout: 'mouseleave'
}, function( orig, fix ){
	jQuery.event.special[ fix ] = {
		setup: function(){
			jQuery.event.add( this, orig, withinElement, fix );
		},
		teardown: function(){
			jQuery.event.remove( this, orig, withinElement );
		}
	};			   
});

jQuery.fn.extend({
	bind: function( type, data, fn ) {
		return type == "unload" ? this.one(type, data, fn) : this.each(function(){
			jQuery.event.add( this, type, fn || data, fn && data );
		});
	},

	one: function( type, data, fn ) {
		var one = jQuery.event.proxy( fn || data, function(event) {
			jQuery(this).unbind(event, one);
			return (fn || data).apply( this, arguments );
		});
		return this.each(function(){
			jQuery.event.add( this, type, one, fn && data);
		});
	},

	unbind: function( type, fn ) {
		return this.each(function(){
			jQuery.event.remove( this, type, fn );
		});
	},

	trigger: function( type, data ) {
		return this.each(function(){
			jQuery.event.trigger( type, data, this );
		});

	},

	triggerHandler: function( type, data ) {
		if( this[0] ){
			var event = jQuery.Event(type);
			event.preventDefault();
			event.stopPropagation();
			jQuery.event.trigger( event, data, this[0] );
			return event.result;
		}		
	},

	toggle: function( fn ) {
		// Save reference to arguments for access in closure
		var args = arguments, i = 1;

		// link all the functions, so any of them can unbind this click handler
		while( i < args.length )
			jQuery.event.proxy( fn, args[i++] );

		return this.click( jQuery.event.proxy( fn, function(event) {
			// Figure out which function to execute
			this.lastToggle = ( this.lastToggle || 0 ) % i;

			// Make sure that clicks stop
			event.preventDefault();

			// and execute the function
			return args[ this.lastToggle++ ].apply( this, arguments ) || false;
		}));
	},

	hover: function(fnOver, fnOut) {
		return this.mouseenter(fnOver).mouseleave(fnOut);
	},

	ready: function(fn) {
		// Attach the listeners
		bindReady();

		// If the DOM is already ready
		if ( jQuery.isReady )
			// Execute the function immediately
			fn.call( document, jQuery );

		// Otherwise, remember the function for later
		else
			// Add the function to the wait list
			jQuery.readyList.push( fn );

		return this;
	},
	
	live: function( type, fn ){
		var proxy = jQuery.event.proxy( fn );
		proxy.guid += this.selector + type;

		jQuery(document).bind( liveConvert(type, this.selector), this.selector, proxy );

		return this;
	},
	
	die: function( type, fn ){
		jQuery(document).unbind( liveConvert(type, this.selector), fn ? { guid: fn.guid + this.selector + type } : null );
		return this;
	}
});

function liveHandler( event ){
	var check = RegExp("(^|\\.)" + event.type + "(\\.|$)"),
		stop = true,
		elems = [];

	jQuery.each(jQuery.data(this, "events").live || [], function(i, fn){
		if ( check.test(fn.type) ) {
			var elem = jQuery(event.target).closest(fn.data)[0];
			if ( elem )
				elems.push({ elem: elem, fn: fn });
		}
	});

	elems.sort(function(a,b) {
		return jQuery.data(a.elem, "closest") - jQuery.data(b.elem, "closest");
	});
	
	jQuery.each(elems, function(){
		if ( this.fn.call(this.elem, event, this.fn.data) === false )
			return (stop = false);
	});

	return stop;
}

function liveConvert(type, selector){
	return ["live", type, selector.replace(/\./g, "`").replace(/ /g, "|")].join(".");
}

jQuery.extend({
	isReady: false,
	readyList: [],
	// Handle when the DOM is ready
	ready: function() {
		// Make sure that the DOM is not already loaded
		if ( !jQuery.isReady ) {
			// Remember that the DOM is ready
			jQuery.isReady = true;

			// If there are functions bound, to execute
			if ( jQuery.readyList ) {
				// Execute all of them
				jQuery.each( jQuery.readyList, function(){
					this.call( document, jQuery );
				});

				// Reset the list of functions
				jQuery.readyList = null;
			}

			// Trigger any bound ready events
			jQuery(document).triggerHandler("ready");
		}
	}
});

var readyBound = false;

function bindReady(){
	if ( readyBound ) return;
	readyBound = true;

	// Mozilla, Opera and webkit nightlies currently support this event
	if ( document.addEventListener ) {
		// Use the handy event callback
		document.addEventListener( "DOMContentLoaded", function(){
			document.removeEventListener( "DOMContentLoaded", arguments.callee, false );
			jQuery.ready();
		}, false );

	// If IE event model is used
	} else if ( document.attachEvent ) {
		// ensure firing before onload,
		// maybe late but safe also for iframes
		document.attachEvent("onreadystatechange", function(){
			if ( document.readyState === "complete" ) {
				document.detachEvent( "onreadystatechange", arguments.callee );
				jQuery.ready();
			}
		});

		// If IE and not an iframe
		// continually check to see if the document is ready
		if ( document.documentElement.doScroll && window == window.top ) (function(){
			if ( jQuery.isReady ) return;

			try {
				// If IE is used, use the trick by Diego Perini
				// http://javascript.nwbox.com/IEContentLoaded/
				document.documentElement.doScroll("left");
			} catch( error ) {
				setTimeout( arguments.callee, 0 );
				return;
			}

			// and execute any waiting functions
			jQuery.ready();
		})();
	}

	// A fallback to window.onload, that will always work
	jQuery.event.add( window, "load", jQuery.ready );
}

jQuery.each( ("blur,focus,load,resize,scroll,unload,click,dblclick," +
	"mousedown,mouseup,mousemove,mouseover,mouseout,mouseenter,mouseleave," +
	"change,select,submit,keydown,keypress,keyup,error").split(","), function(i, name){

	// Handle event binding
	jQuery.fn[name] = function(fn){
		return fn ? this.bind(name, fn) : this.trigger(name);
	};
});

// Prevent memory leaks in IE
// And prevent errors on refresh with events like mouseover in other browsers
// Window isn't included so as not to unbind existing unload events
jQuery( window ).bind( 'unload', function(){ 
	for ( var id in jQuery.cache )
		// Skip the window
		if ( id != 1 && jQuery.cache[ id ].handle )
			jQuery.event.remove( jQuery.cache[ id ].handle.elem );
}); 
(function(){

	jQuery.support = {};

	var root = document.documentElement,
		script = document.createElement("script"),
		div = document.createElement("div"),
		id = "script" + (new Date).getTime();

	div.style.display = "none";
	div.innerHTML = '   <link/><table></table><a href="/a" style="color:red;float:left;opacity:.5;">a</a><select><option>text</option></select><object><param/></object>';

	var all = div.getElementsByTagName("*"),
		a = div.getElementsByTagName("a")[0];

	// Can't get basic test support
	if ( !all || !all.length || !a ) {
		return;
	}

	jQuery.support = {
		// IE strips leading whitespace when .innerHTML is used
		leadingWhitespace: div.firstChild.nodeType == 3,
		
		// Make sure that tbody elements aren't automatically inserted
		// IE will insert them into empty tables
		tbody: !div.getElementsByTagName("tbody").length,
		
		// Make sure that you can get all elements in an <object> element
		// IE 7 always returns no results
		objectAll: !!div.getElementsByTagName("object")[0]
			.getElementsByTagName("*").length,
		
		// Make sure that link elements get serialized correctly by innerHTML
		// This requires a wrapper element in IE
		htmlSerialize: !!div.getElementsByTagName("link").length,
		
		// Get the style information from getAttribute
		// (IE uses .cssText insted)
		style: /red/.test( a.getAttribute("style") ),
		
		// Make sure that URLs aren't manipulated
		// (IE normalizes it by default)
		hrefNormalized: a.getAttribute("href") === "/a",
		
		// Make sure that element opacity exists
		// (IE uses filter instead)
		opacity: a.style.opacity === "0.5",
		
		// Verify style float existence
		// (IE uses styleFloat instead of cssFloat)
		cssFloat: !!a.style.cssFloat,

		// Will be defined later
		scriptEval: false,
		noCloneEvent: true,
		boxModel: null
	};
	
	script.type = "text/javascript";
	try {
		script.appendChild( document.createTextNode( "window." + id + "=1;" ) );
	} catch(e){}

	root.insertBefore( script, root.firstChild );
	
	// Make sure that the execution of code works by injecting a script
	// tag with appendChild/createTextNode
	// (IE doesn't support this, fails, and uses .text instead)
	if ( window[ id ] ) {
		jQuery.support.scriptEval = true;
		delete window[ id ];
	}

	root.removeChild( script );

	if ( div.attachEvent && div.fireEvent ) {
		div.attachEvent("onclick", function(){
			// Cloning a node shouldn't copy over any
			// bound event handlers (IE does this)
			jQuery.support.noCloneEvent = false;
			div.detachEvent("onclick", arguments.callee);
		});
		div.cloneNode(true).fireEvent("onclick");
	}

	// Figure out if the W3C box model works as expected
	// document.body must exist before we can do this
	jQuery(function(){
		var div = document.createElement("div");
		div.style.width = div.style.paddingLeft = "1px";

		document.body.appendChild( div );
		jQuery.boxModel = jQuery.support.boxModel = div.offsetWidth === 2;
		document.body.removeChild( div ).style.display = 'none';
	});
})();

var styleFloat = jQuery.support.cssFloat ? "cssFloat" : "styleFloat";

jQuery.props = {
	"for": "htmlFor",
	"class": "className",
	"float": styleFloat,
	cssFloat: styleFloat,
	styleFloat: styleFloat,
	readonly: "readOnly",
	maxlength: "maxLength",
	cellspacing: "cellSpacing",
	rowspan: "rowSpan",
	tabindex: "tabIndex"
};
jQuery.fn.extend({
	// Keep a copy of the old load
	_load: jQuery.fn.load,

	load: function( url, params, callback ) {
		if ( typeof url !== "string" )
			return this._load( url );

		var off = url.indexOf(" ");
		if ( off >= 0 ) {
			var selector = url.slice(off, url.length);
			url = url.slice(0, off);
		}

		// Default to a GET request
		var type = "GET";

		// If the second parameter was provided
		if ( params )
			// If it's a function
			if ( jQuery.isFunction( params ) ) {
				// We assume that it's the callback
				callback = params;
				params = null;

			// Otherwise, build a param string
			} else if( typeof params === "object" ) {
				params = jQuery.param( params );
				type = "POST";
			}

		var self = this;

		// Request the remote document
		jQuery.ajax({
			url: url,
			type: type,
			dataType: "html",
			data: params,
			complete: function(res, status){
				// If successful, inject the HTML into all the matched elements
				if ( status == "success" || status == "notmodified" )
					// See if a selector was specified
					self.html( selector ?
						// Create a dummy div to hold the results
						jQuery("<div/>")
							// inject the contents of the document in, removing the scripts
							// to avoid any 'Permission Denied' errors in IE
							.append(res.responseText.replace(/<script(.|\s)*?\/script>/g, ""))

							// Locate the specified elements
							.find(selector) :

						// If not, just inject the full result
						res.responseText );

				if( callback )
					self.each( callback, [res.responseText, status, res] );
			}
		});
		return this;
	},

	serialize: function() {
		return jQuery.param(this.serializeArray());
	},
	serializeArray: function() {
		return this.map(function(){
			return this.elements ? jQuery.makeArray(this.elements) : this;
		})
		.filter(function(){
			return this.name && !this.disabled &&
				(this.checked || /select|textarea/i.test(this.nodeName) ||
					/text|hidden|password|search/i.test(this.type));
		})
		.map(function(i, elem){
			var val = jQuery(this).val();
			return val == null ? null :
				jQuery.isArray(val) ?
					jQuery.map( val, function(val, i){
						return {name: elem.name, value: val};
					}) :
					{name: elem.name, value: val};
		}).get();
	}
});

// Attach a bunch of functions for handling common AJAX events
jQuery.each( "ajaxStart,ajaxStop,ajaxComplete,ajaxError,ajaxSuccess,ajaxSend".split(","), function(i,o){
	jQuery.fn[o] = function(f){
		return this.bind(o, f);
	};
});

var jsc = now();

jQuery.extend({
  
	get: function( url, data, callback, type ) {
		// shift arguments if data argument was ommited
		if ( jQuery.isFunction( data ) ) {
			callback = data;
			data = null;
		}

		return jQuery.ajax({
			type: "GET",
			url: url,
			data: data,
			success: callback,
			dataType: type
		});
	},

	getScript: function( url, callback ) {
		return jQuery.get(url, null, callback, "script");
	},

	getJSON: function( url, data, callback ) {
		return jQuery.get(url, data, callback, "json");
	},

	post: function( url, data, callback, type ) {
		if ( jQuery.isFunction( data ) ) {
			callback = data;
			data = {};
		}

		return jQuery.ajax({
			type: "POST",
			url: url,
			data: data,
			success: callback,
			dataType: type
		});
	},

	ajaxSetup: function( settings ) {
		jQuery.extend( jQuery.ajaxSettings, settings );
	},

	ajaxSettings: {
		url: location.href,
		global: true,
		type: "GET",
		contentType: "application/x-www-form-urlencoded",
		processData: true,
		async: true,
		/*
		timeout: 0,
		data: null,
		username: null,
		password: null,
		*/
		// Create the request object; Microsoft failed to properly
		// implement the XMLHttpRequest in IE7, so we use the ActiveXObject when it is available
		// This function can be overriden by calling jQuery.ajaxSetup
		xhr:function(){
			return window.ActiveXObject ? new ActiveXObject("Microsoft.XMLHTTP") : new XMLHttpRequest();
		},
		accepts: {
			xml: "application/xml, text/xml",
			html: "text/html",
			script: "text/javascript, application/javascript",
			json: "application/json, text/javascript",
			text: "text/plain",
			_default: "*/*"
		}
	},

	// Last-Modified header cache for next request
	lastModified: {},

	ajax: function( s ) {
		// Extend the settings, but re-extend 's' so that it can be
		// checked again later (in the test suite, specifically)
		s = jQuery.extend(true, s, jQuery.extend(true, {}, jQuery.ajaxSettings, s));

		var jsonp, jsre = /=\?(&|$)/g, status, data,
			type = s.type.toUpperCase();

		// convert data if not already a string
		if ( s.data && s.processData && typeof s.data !== "string" )
			s.data = jQuery.param(s.data);

		// Handle JSONP Parameter Callbacks
		if ( s.dataType == "jsonp" ) {
			if ( type == "GET" ) {
				if ( !s.url.match(jsre) )
					s.url += (s.url.match(/\?/) ? "&" : "?") + (s.jsonp || "callback") + "=?";
			} else if ( !s.data || !s.data.match(jsre) )
				s.data = (s.data ? s.data + "&" : "") + (s.jsonp || "callback") + "=?";
			s.dataType = "json";
		}

		// Build temporary JSONP function
		if ( s.dataType == "json" && (s.data && s.data.match(jsre) || s.url.match(jsre)) ) {
			jsonp = "jsonp" + jsc++;

			// Replace the =? sequence both in the query string and the data
			if ( s.data )
				s.data = (s.data + "").replace(jsre, "=" + jsonp + "$1");
			s.url = s.url.replace(jsre, "=" + jsonp + "$1");

			// We need to make sure
			// that a JSONP style response is executed properly
			s.dataType = "script";

			// Handle JSONP-style loading
			window[ jsonp ] = function(tmp){
				data = tmp;
				success();
				complete();
				// Garbage collect
				window[ jsonp ] = undefined;
				try{ delete window[ jsonp ]; } catch(e){}
				if ( head )
					head.removeChild( script );
			};
		}

		if ( s.dataType == "script" && s.cache == null )
			s.cache = false;

		if ( s.cache === false && type == "GET" ) {
			var ts = now();
			// try replacing _= if it is there
			var ret = s.url.replace(/(\?|&)_=.*?(&|$)/, "$1_=" + ts + "$2");
			// if nothing was replaced, add timestamp to the end
			s.url = ret + ((ret == s.url) ? (s.url.match(/\?/) ? "&" : "?") + "_=" + ts : "");
		}

		// If data is available, append data to url for get requests
		if ( s.data && type == "GET" ) {
			s.url += (s.url.match(/\?/) ? "&" : "?") + s.data;

			// IE likes to send both get and post data, prevent this
			s.data = null;
		}

		// Watch for a new set of requests
		if ( s.global && ! jQuery.active++ )
			jQuery.event.trigger( "ajaxStart" );

		// Matches an absolute URL, and saves the domain
		var parts = /^(\w+:)?\/\/([^\/?#]+)/.exec( s.url );

		// If we're requesting a remote document
		// and trying to load JSON or Script with a GET
		if ( s.dataType == "script" && type == "GET" && parts
			&& ( parts[1] && parts[1] != location.protocol || parts[2] != location.host )){

			var head = document.getElementsByTagName("head")[0];
			var script = document.createElement("script");
			script.src = s.url;
			if (s.scriptCharset)
				script.charset = s.scriptCharset;

			// Handle Script loading
			if ( !jsonp ) {
				var done = false;

				// Attach handlers for all browsers
				script.onload = script.onreadystatechange = function(){
					if ( !done && (!this.readyState ||
							this.readyState == "loaded" || this.readyState == "complete") ) {
						done = true;
						success();
						complete();

						// Handle memory leak in IE
						script.onload = script.onreadystatechange = null;
						head.removeChild( script );
					}
				};
			}

			head.appendChild(script);

			// We handle everything using the script element injection
			return undefined;
		}

		var requestDone = false;

		// Create the request object
		var xhr = s.xhr();

		// Open the socket
		// Passing null username, generates a login popup on Opera (#2865)
		if( s.username )
			xhr.open(type, s.url, s.async, s.username, s.password);
		else
			xhr.open(type, s.url, s.async);

		// Need an extra try/catch for cross domain requests in Firefox 3
		try {
			// Set the correct header, if data is being sent
			if ( s.data )
				xhr.setRequestHeader("Content-Type", s.contentType);

			// Set the If-Modified-Since header, if ifModified mode.
			if ( s.ifModified )
				xhr.setRequestHeader("If-Modified-Since",
					jQuery.lastModified[s.url] || "Thu, 01 Jan 1970 00:00:00 GMT" );

			// Set header so the called script knows that it's an XMLHttpRequest
			xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");

			// Set the Accepts header for the server, depending on the dataType
			xhr.setRequestHeader("Accept", s.dataType && s.accepts[ s.dataType ] ?
				s.accepts[ s.dataType ] + ", */*" :
				s.accepts._default );
		} catch(e){}

		// Allow custom headers/mimetypes and early abort
		if ( s.beforeSend && s.beforeSend(xhr, s) === false ) {
			// Handle the global AJAX counter
			if ( s.global && ! --jQuery.active )
				jQuery.event.trigger( "ajaxStop" );
			// close opended socket
			xhr.abort();
			return false;
		}

		if ( s.global )
			jQuery.event.trigger("ajaxSend", [xhr, s]);

		// Wait for a response to come back
		var onreadystatechange = function(isTimeout){
			// The request was aborted, clear the interval and decrement jQuery.active
			if (xhr.readyState == 0) {
				if (ival) {
					// clear poll interval
					clearInterval(ival);
					ival = null;
					// Handle the global AJAX counter
					if ( s.global && ! --jQuery.active )
						jQuery.event.trigger( "ajaxStop" );
				}
			// The transfer is complete and the data is available, or the request timed out
			} else if ( !requestDone && xhr && (xhr.readyState == 4 || isTimeout == "timeout") ) {
				requestDone = true;

				// clear poll interval
				if (ival) {
					clearInterval(ival);
					ival = null;
				}

				status = isTimeout == "timeout" ? "timeout" :
					!jQuery.httpSuccess( xhr ) ? "error" :
					s.ifModified && jQuery.httpNotModified( xhr, s.url ) ? "notmodified" :
					"success";

				if ( status == "success" ) {
					// Watch for, and catch, XML document parse errors
					try {
						// process the data (runs the xml through httpData regardless of callback)
						data = jQuery.httpData( xhr, s.dataType, s );
					} catch(e) {
						status = "parsererror";
					}
				}

				// Make sure that the request was successful or notmodified
				if ( status == "success" ) {
					// Cache Last-Modified header, if ifModified mode.
					var modRes;
					try {
						modRes = xhr.getResponseHeader("Last-Modified");
					} catch(e) {} // swallow exception thrown by FF if header is not available

					if ( s.ifModified && modRes )
						jQuery.lastModified[s.url] = modRes;

					// JSONP handles its own success callback
					if ( !jsonp )
						success();
				} else
					jQuery.handleError(s, xhr, status);

				// Fire the complete handlers
				complete();

				if ( isTimeout )
					xhr.abort();

				// Stop memory leaks
				if ( s.async )
					xhr = null;
			}
		};

		if ( s.async ) {
			// don't attach the handler to the request, just poll it instead
			var ival = setInterval(onreadystatechange, 13);

			// Timeout checker
			if ( s.timeout > 0 )
				setTimeout(function(){
					// Check to see if the request is still happening
					if ( xhr && !requestDone )
						onreadystatechange( "timeout" );
				}, s.timeout);
		}

		// Send the data
		try {
			xhr.send(s.data);
		} catch(e) {
			jQuery.handleError(s, xhr, null, e);
		}

		// firefox 1.5 doesn't fire statechange for sync requests
		if ( !s.async )
			onreadystatechange();

		function success(){
			// If a local callback was specified, fire it and pass it the data
			if ( s.success )
				s.success( data, status );

			// Fire the global callback
			if ( s.global )
				jQuery.event.trigger( "ajaxSuccess", [xhr, s] );
		}

		function complete(){
			// Process result
			if ( s.complete )
				s.complete(xhr, status);

			// The request was completed
			if ( s.global )
				jQuery.event.trigger( "ajaxComplete", [xhr, s] );

			// Handle the global AJAX counter
			if ( s.global && ! --jQuery.active )
				jQuery.event.trigger( "ajaxStop" );
		}

		// return XMLHttpRequest to allow aborting the request etc.
		return xhr;
	},

	handleError: function( s, xhr, status, e ) {
		// If a local callback was specified, fire it
		if ( s.error ) s.error( xhr, status, e );

		// Fire the global callback
		if ( s.global )
			jQuery.event.trigger( "ajaxError", [xhr, s, e] );
	},

	// Counter for holding the number of active queries
	active: 0,

	// Determines if an XMLHttpRequest was successful or not
	httpSuccess: function( xhr ) {
		try {
			// IE error sometimes returns 1223 when it should be 204 so treat it as success, see #1450
			return !xhr.status && location.protocol == "file:" ||
				( xhr.status >= 200 && xhr.status < 300 ) || xhr.status == 304 || xhr.status == 1223;
		} catch(e){}
		return false;
	},

	// Determines if an XMLHttpRequest returns NotModified
	httpNotModified: function( xhr, url ) {
		try {
			var xhrRes = xhr.getResponseHeader("Last-Modified");

			// Firefox always returns 200. check Last-Modified date
			return xhr.status == 304 || xhrRes == jQuery.lastModified[url];
		} catch(e){}
		return false;
	},

	httpData: function( xhr, type, s ) {
		var ct = xhr.getResponseHeader("content-type"),
			xml = type == "xml" || !type && ct && ct.indexOf("xml") >= 0,
			data = xml ? xhr.responseXML : xhr.responseText;

		if ( xml && data.documentElement.tagName == "parsererror" )
			throw "parsererror";
			
		// Allow a pre-filtering function to sanitize the response
		// s != null is checked to keep backwards compatibility
		if( s && s.dataFilter )
			data = s.dataFilter( data, type );

		// The filter can actually parse the response
		if( typeof data === "string" ){

			// If the type is "script", eval it in global context
			if ( type == "script" )
				jQuery.globalEval( data );

			// Get the JavaScript object, if JSON is used.
			if ( type == "json" )
				data = window["eval"]("(" + data + ")");
		}
		
		return data;
	},

	// Serialize an array of form elements or a set of
	// key/values into a query string
	param: function( a ) {
		var s = [ ];

		function add( key, value ){
			s[ s.length ] = encodeURIComponent(key) + '=' + encodeURIComponent(value);
		};

		// If an array was passed in, assume that it is an array
		// of form elements
		if ( jQuery.isArray(a) || a.jquery )
			// Serialize the form elements
			jQuery.each( a, function(){
				add( this.name, this.value );
			});

		// Otherwise, assume that it's an object of key/value pairs
		else
			// Serialize the key/values
			for ( var j in a )
				// If the value is an array then the key names need to be repeated
				if ( jQuery.isArray(a[j]) )
					jQuery.each( a[j], function(){
						add( j, this );
					});
				else
					add( j, jQuery.isFunction(a[j]) ? a[j]() : a[j] );

		// Return the resulting serialization
		return s.join("&").replace(/%20/g, "+");
	}

});
var elemdisplay = {},
	timerId,
	fxAttrs = [
		// height animations
		[ "height", "marginTop", "marginBottom", "paddingTop", "paddingBottom" ],
		// width animations
		[ "width", "marginLeft", "marginRight", "paddingLeft", "paddingRight" ],
		// opacity animations
		[ "opacity" ]
	];

function genFx( type, num ){
	var obj = {};
	jQuery.each( fxAttrs.concat.apply([], fxAttrs.slice(0,num)), function(){
		obj[ this ] = type;
	});
	return obj;
}

jQuery.fn.extend({
	show: function(speed,callback){
		if ( speed ) {
			return this.animate( genFx("show", 3), speed, callback);
		} else {
			for ( var i = 0, l = this.length; i < l; i++ ){
				var old = jQuery.data(this[i], "olddisplay");
				
				this[i].style.display = old || "";
				
				if ( jQuery.css(this[i], "display") === "none" ) {
					var tagName = this[i].tagName, display;
					
					if ( elemdisplay[ tagName ] ) {
						display = elemdisplay[ tagName ];
					} else {
						var elem = jQuery("<" + tagName + " />").appendTo("body");
						
						display = elem.css("display");
						if ( display === "none" )
							display = "block";
						
						elem.remove();
						
						elemdisplay[ tagName ] = display;
					}
					
					jQuery.data(this[i], "olddisplay", display);
				}
			}

			// Set the display of the elements in a second loop
			// to avoid the constant reflow
			for ( var i = 0, l = this.length; i < l; i++ ){
				this[i].style.display = jQuery.data(this[i], "olddisplay") || "";
			}
			
			return this;
		}
	},

	hide: function(speed,callback){
		if ( speed ) {
			return this.animate( genFx("hide", 3), speed, callback);
		} else {
			for ( var i = 0, l = this.length; i < l; i++ ){
				var old = jQuery.data(this[i], "olddisplay");
				if ( !old && old !== "none" )
					jQuery.data(this[i], "olddisplay", jQuery.css(this[i], "display"));
			}

			// Set the display of the elements in a second loop
			// to avoid the constant reflow
			for ( var i = 0, l = this.length; i < l; i++ ){
				this[i].style.display = "none";
			}

			return this;
		}
	},

	// Save the old toggle function
	_toggle: jQuery.fn.toggle,

	toggle: function( fn, fn2 ){
		var bool = typeof fn === "boolean";

		return jQuery.isFunction(fn) && jQuery.isFunction(fn2) ?
			this._toggle.apply( this, arguments ) :
			fn == null || bool ?
				this.each(function(){
					var state = bool ? fn : jQuery(this).is(":hidden");
					jQuery(this)[ state ? "show" : "hide" ]();
				}) :
				this.animate(genFx("toggle", 3), fn, fn2);
	},

	fadeTo: function(speed,to,callback){
		return this.animate({opacity: to}, speed, callback);
	},

	animate: function( prop, speed, easing, callback ) {
		var optall = jQuery.speed(speed, easing, callback);

		return this[ optall.queue === false ? "each" : "queue" ](function(){
		
			var opt = jQuery.extend({}, optall), p,
				hidden = this.nodeType == 1 && jQuery(this).is(":hidden"),
				self = this;
	
			for ( p in prop ) {
				if ( prop[p] == "hide" && hidden || prop[p] == "show" && !hidden )
					return opt.complete.call(this);

				if ( ( p == "height" || p == "width" ) && this.style ) {
					// Store display property
					opt.display = jQuery.css(this, "display");

					// Make sure that nothing sneaks out
					opt.overflow = this.style.overflow;
				}
			}

			if ( opt.overflow != null )
				this.style.overflow = "hidden";

			opt.curAnim = jQuery.extend({}, prop);

			jQuery.each( prop, function(name, val){
				var e = new jQuery.fx( self, opt, name );

				if ( /toggle|show|hide/.test(val) )
					e[ val == "toggle" ? hidden ? "show" : "hide" : val ]( prop );
				else {
					var parts = val.toString().match(/^([+-]=)?([\d+-.]+)(.*)$/),
						start = e.cur(true) || 0;

					if ( parts ) {
						var end = parseFloat(parts[2]),
							unit = parts[3] || "px";

						// We need to compute starting value
						if ( unit != "px" ) {
							self.style[ name ] = (end || 1) + unit;
							start = ((end || 1) / e.cur(true)) * start;
							self.style[ name ] = start + unit;
						}

						// If a +=/-= token was provided, we're doing a relative animation
						if ( parts[1] )
							end = ((parts[1] == "-=" ? -1 : 1) * end) + start;

						e.custom( start, end, unit );
					} else
						e.custom( start, val, "" );
				}
			});

			// For JS strict compliance
			return true;
		});
	},

	stop: function(clearQueue, gotoEnd){
		var timers = jQuery.timers;

		if (clearQueue)
			this.queue([]);

		this.each(function(){
			// go in reverse order so anything added to the queue during the loop is ignored
			for ( var i = timers.length - 1; i >= 0; i-- )
				if ( timers[i].elem == this ) {
					if (gotoEnd)
						// force the next step to be the last
						timers[i](true);
					timers.splice(i, 1);
				}
		});

		// start the next in the queue if the last step wasn't forced
		if (!gotoEnd)
			this.dequeue();

		return this;
	}

});

// Generate shortcuts for custom animations
jQuery.each({
	slideDown: genFx("show", 1),
	slideUp: genFx("hide", 1),
	slideToggle: genFx("toggle", 1),
	fadeIn: { opacity: "show" },
	fadeOut: { opacity: "hide" }
}, function( name, props ){
	jQuery.fn[ name ] = function( speed, callback ){
		return this.animate( props, speed, callback );
	};
});

jQuery.extend({

	speed: function(speed, easing, fn) {
		var opt = typeof speed === "object" ? speed : {
			complete: fn || !fn && easing ||
				jQuery.isFunction( speed ) && speed,
			duration: speed,
			easing: fn && easing || easing && !jQuery.isFunction(easing) && easing
		};

		opt.duration = jQuery.fx.off ? 0 : typeof opt.duration === "number" ? opt.duration :
			jQuery.fx.speeds[opt.duration] || jQuery.fx.speeds._default;

		// Queueing
		opt.old = opt.complete;
		opt.complete = function(){
			if ( opt.queue !== false )
				jQuery(this).dequeue();
			if ( jQuery.isFunction( opt.old ) )
				opt.old.call( this );
		};

		return opt;
	},

	easing: {
		linear: function( p, n, firstNum, diff ) {
			return firstNum + diff * p;
		},
		swing: function( p, n, firstNum, diff ) {
			return ((-Math.cos(p*Math.PI)/2) + 0.5) * diff + firstNum;
		}
	},

	timers: [],

	fx: function( elem, options, prop ){
		this.options = options;
		this.elem = elem;
		this.prop = prop;

		if ( !options.orig )
			options.orig = {};
	}

});

jQuery.fx.prototype = {

	// Simple function for setting a style value
	update: function(){
		if ( this.options.step )
			this.options.step.call( this.elem, this.now, this );

		(jQuery.fx.step[this.prop] || jQuery.fx.step._default)( this );

		// Set display property to block for height/width animations
		if ( ( this.prop == "height" || this.prop == "width" ) && this.elem.style )
			this.elem.style.display = "block";
	},

	// Get the current size
	cur: function(force){
		if ( this.elem[this.prop] != null && (!this.elem.style || this.elem.style[this.prop] == null) )
			return this.elem[ this.prop ];

		var r = parseFloat(jQuery.css(this.elem, this.prop, force));
		return r && r > -10000 ? r : parseFloat(jQuery.curCSS(this.elem, this.prop)) || 0;
	},

	// Start an animation from one number to another
	custom: function(from, to, unit){
		this.startTime = now();
		this.start = from;
		this.end = to;
		this.unit = unit || this.unit || "px";
		this.now = this.start;
		this.pos = this.state = 0;

		var self = this;
		function t(gotoEnd){
			return self.step(gotoEnd);
		}

		t.elem = this.elem;

		if ( t() && jQuery.timers.push(t) && !timerId ) {
			timerId = setInterval(function(){
				var timers = jQuery.timers;

				for ( var i = 0; i < timers.length; i++ )
					if ( !timers[i]() )
						timers.splice(i--, 1);

				if ( !timers.length ) {
					clearInterval( timerId );
					timerId = undefined;
				}
			}, 13);
		}
	},

	// Simple 'show' function
	show: function(){
		// Remember where we started, so that we can go back to it later
		this.options.orig[this.prop] = jQuery.attr( this.elem.style, this.prop );
		this.options.show = true;

		// Begin the animation
		// Make sure that we start at a small width/height to avoid any
		// flash of content
		this.custom(this.prop == "width" || this.prop == "height" ? 1 : 0, this.cur());

		// Start by showing the element
		jQuery(this.elem).show();
	},

	// Simple 'hide' function
	hide: function(){
		// Remember where we started, so that we can go back to it later
		this.options.orig[this.prop] = jQuery.attr( this.elem.style, this.prop );
		this.options.hide = true;

		// Begin the animation
		this.custom(this.cur(), 0);
	},

	// Each step of an animation
	step: function(gotoEnd){
		var t = now();

		if ( gotoEnd || t >= this.options.duration + this.startTime ) {
			this.now = this.end;
			this.pos = this.state = 1;
			this.update();

			this.options.curAnim[ this.prop ] = true;

			var done = true;
			for ( var i in this.options.curAnim )
				if ( this.options.curAnim[i] !== true )
					done = false;

			if ( done ) {
				if ( this.options.display != null ) {
					// Reset the overflow
					this.elem.style.overflow = this.options.overflow;

					// Reset the display
					this.elem.style.display = this.options.display;
					if ( jQuery.css(this.elem, "display") == "none" )
						this.elem.style.display = "block";
				}

				// Hide the element if the "hide" operation was done
				if ( this.options.hide )
					jQuery(this.elem).hide();

				// Reset the properties, if the item has been hidden or shown
				if ( this.options.hide || this.options.show )
					for ( var p in this.options.curAnim )
						jQuery.attr(this.elem.style, p, this.options.orig[p]);
					
				// Execute the complete function
				this.options.complete.call( this.elem );
			}

			return false;
		} else {
			var n = t - this.startTime;
			this.state = n / this.options.duration;

			// Perform the easing function, defaults to swing
			this.pos = jQuery.easing[this.options.easing || (jQuery.easing.swing ? "swing" : "linear")](this.state, n, 0, 1, this.options.duration);
			this.now = this.start + ((this.end - this.start) * this.pos);

			// Perform the next step of the animation
			this.update();
		}

		return true;
	}

};

jQuery.extend( jQuery.fx, {
	speeds:{
		slow: 600,
 		fast: 200,
 		// Default speed
 		_default: 400
	},
	step: {

		opacity: function(fx){
			jQuery.attr(fx.elem.style, "opacity", fx.now);
		},

		_default: function(fx){
			if ( fx.elem.style && fx.elem.style[ fx.prop ] != null )
				fx.elem.style[ fx.prop ] = fx.now + fx.unit;
			else
				fx.elem[ fx.prop ] = fx.now;
		}
	}
});
if ( document.documentElement["getBoundingClientRect"] )
	jQuery.fn.offset = function() {
		if ( !this[0] ) return { top: 0, left: 0 };
		if ( this[0] === this[0].ownerDocument.body ) return jQuery.offset.bodyOffset( this[0] );
		var box  = this[0].getBoundingClientRect(), doc = this[0].ownerDocument, body = doc.body, docElem = doc.documentElement,
			clientTop = docElem.clientTop || body.clientTop || 0, clientLeft = docElem.clientLeft || body.clientLeft || 0,
			top  = box.top  + (self.pageYOffset || jQuery.boxModel && docElem.scrollTop  || body.scrollTop ) - clientTop,
			left = box.left + (self.pageXOffset || jQuery.boxModel && docElem.scrollLeft || body.scrollLeft) - clientLeft;
		return { top: top, left: left };
	};
else 
	jQuery.fn.offset = function() {
		if ( !this[0] ) return { top: 0, left: 0 };
		if ( this[0] === this[0].ownerDocument.body ) return jQuery.offset.bodyOffset( this[0] );
		jQuery.offset.initialized || jQuery.offset.initialize();

		var elem = this[0], offsetParent = elem.offsetParent, prevOffsetParent = elem,
			doc = elem.ownerDocument, computedStyle, docElem = doc.documentElement,
			body = doc.body, defaultView = doc.defaultView,
			prevComputedStyle = defaultView.getComputedStyle(elem, null),
			top = elem.offsetTop, left = elem.offsetLeft;

		while ( (elem = elem.parentNode) && elem !== body && elem !== docElem ) {
			computedStyle = defaultView.getComputedStyle(elem, null);
			top -= elem.scrollTop, left -= elem.scrollLeft;
			if ( elem === offsetParent ) {
				top += elem.offsetTop, left += elem.offsetLeft;
				if ( jQuery.offset.doesNotAddBorder && !(jQuery.offset.doesAddBorderForTableAndCells && /^t(able|d|h)$/i.test(elem.tagName)) )
					top  += parseInt( computedStyle.borderTopWidth,  10) || 0,
					left += parseInt( computedStyle.borderLeftWidth, 10) || 0;
				prevOffsetParent = offsetParent, offsetParent = elem.offsetParent;
			}
			if ( jQuery.offset.subtractsBorderForOverflowNotVisible && computedStyle.overflow !== "visible" )
				top  += parseInt( computedStyle.borderTopWidth,  10) || 0,
				left += parseInt( computedStyle.borderLeftWidth, 10) || 0;
			prevComputedStyle = computedStyle;
		}

		if ( prevComputedStyle.position === "relative" || prevComputedStyle.position === "static" )
			top  += body.offsetTop,
			left += body.offsetLeft;

		if ( prevComputedStyle.position === "fixed" )
			top  += Math.max(docElem.scrollTop, body.scrollTop),
			left += Math.max(docElem.scrollLeft, body.scrollLeft);

		return { top: top, left: left };
	};

jQuery.offset = {
	initialize: function() {
		if ( this.initialized ) return;
		var body = document.body, container = document.createElement('div'), innerDiv, checkDiv, table, td, rules, prop, bodyMarginTop = body.style.marginTop,
			html = '<div style="position:absolute;top:0;left:0;margin:0;border:5px solid #000;padding:0;width:1px;height:1px;"><div></div></div><table style="position:absolute;top:0;left:0;margin:0;border:5px solid #000;padding:0;width:1px;height:1px;" cellpadding="0" cellspacing="0"><tr><td></td></tr></table>';

		rules = { position: 'absolute', top: 0, left: 0, margin: 0, border: 0, width: '1px', height: '1px', visibility: 'hidden' };
		for ( prop in rules ) container.style[prop] = rules[prop];

		container.innerHTML = html;
		body.insertBefore(container, body.firstChild);
		innerDiv = container.firstChild, checkDiv = innerDiv.firstChild, td = innerDiv.nextSibling.firstChild.firstChild;

		this.doesNotAddBorder = (checkDiv.offsetTop !== 5);
		this.doesAddBorderForTableAndCells = (td.offsetTop === 5);

		innerDiv.style.overflow = 'hidden', innerDiv.style.position = 'relative';
		this.subtractsBorderForOverflowNotVisible = (checkDiv.offsetTop === -5);

		body.style.marginTop = '1px';
		this.doesNotIncludeMarginInBodyOffset = (body.offsetTop === 0);
		body.style.marginTop = bodyMarginTop;

		body.removeChild(container);
		this.initialized = true;
	},

	bodyOffset: function(body) {
		jQuery.offset.initialized || jQuery.offset.initialize();
		var top = body.offsetTop, left = body.offsetLeft;
		if ( jQuery.offset.doesNotIncludeMarginInBodyOffset )
			top  += parseInt( jQuery.curCSS(body, 'marginTop',  true), 10 ) || 0,
			left += parseInt( jQuery.curCSS(body, 'marginLeft', true), 10 ) || 0;
		return { top: top, left: left };
	}
};


jQuery.fn.extend({
	position: function() {
		var left = 0, top = 0, results;

		if ( this[0] ) {
			// Get *real* offsetParent
			var offsetParent = this.offsetParent(),

			// Get correct offsets
			offset       = this.offset(),
			parentOffset = /^body|html$/i.test(offsetParent[0].tagName) ? { top: 0, left: 0 } : offsetParent.offset();

			// Subtract element margins
			// note: when an element has margin: auto the offsetLeft and marginLeft 
			// are the same in Safari causing offset.left to incorrectly be 0
			offset.top  -= num( this, 'marginTop'  );
			offset.left -= num( this, 'marginLeft' );

			// Add offsetParent borders
			parentOffset.top  += num( offsetParent, 'borderTopWidth'  );
			parentOffset.left += num( offsetParent, 'borderLeftWidth' );

			// Subtract the two offsets
			results = {
				top:  offset.top  - parentOffset.top,
				left: offset.left - parentOffset.left
			};
		}

		return results;
	},

	offsetParent: function() {
		var offsetParent = this[0].offsetParent || document.body;
		while ( offsetParent && (!/^body|html$/i.test(offsetParent.tagName) && jQuery.css(offsetParent, 'position') == 'static') )
			offsetParent = offsetParent.offsetParent;
		return jQuery(offsetParent);
	}
});


// Create scrollLeft and scrollTop methods
jQuery.each( ['Left', 'Top'], function(i, name) {
	var method = 'scroll' + name;
	
	jQuery.fn[ method ] = function(val) {
		if (!this[0]) return null;

		return val !== undefined ?

			// Set the scroll offset
			this.each(function() {
				this == window || this == document ?
					window.scrollTo(
						!i ? val : jQuery(window).scrollLeft(),
						 i ? val : jQuery(window).scrollTop()
					) :
					this[ method ] = val;
			}) :

			// Return the scroll offset
			this[0] == window || this[0] == document ?
				self[ i ? 'pageYOffset' : 'pageXOffset' ] ||
					jQuery.boxModel && document.documentElement[ method ] ||
					document.body[ method ] :
				this[0][ method ];
	};
});
// Create innerHeight, innerWidth, outerHeight and outerWidth methods
jQuery.each([ "Height", "Width" ], function(i, name){

	var tl = i ? "Left"  : "Top",  // top or left
		br = i ? "Right" : "Bottom", // bottom or right
		lower = name.toLowerCase();

	// innerHeight and innerWidth
	jQuery.fn["inner" + name] = function(){
		return this[0] ?
			jQuery.css( this[0], lower, false, "padding" ) :
			null;
	};

	// outerHeight and outerWidth
	jQuery.fn["outer" + name] = function(margin) {
		return this[0] ?
			jQuery.css( this[0], lower, false, margin ? "margin" : "border" ) :
			null;
	};
	
	var type = name.toLowerCase();

	jQuery.fn[ type ] = function( size ) {
		// Get window width or height
		return this[0] == window ?
			// Everyone else use document.documentElement or document.body depending on Quirks vs Standards mode
			document.compatMode == "CSS1Compat" && document.documentElement[ "client" + name ] ||
			document.body[ "client" + name ] :

			// Get document width or height
			this[0] == document ?
				// Either scroll[Width/Height] or offset[Width/Height], whichever is greater
				Math.max(
					document.documentElement["client" + name],
					document.body["scroll" + name], document.documentElement["scroll" + name],
					document.body["offset" + name], document.documentElement["offset" + name]
				) :

				// Get or set width or height on the element
				size === undefined ?
					// Get width or height on the element
					(this.length ? jQuery.css( this[0], type ) : null) :

					// Set the width or height on the element (default to pixels if value is unitless)
					this.css( type, typeof size === "string" ? size : size + "px" );
	};

});
})();


/* Jquery use for Oracle Help URL script Start */
jQuery(document).ready(function() {
//alert($("a")[3].onclick);
var $td = $("td");
var alength = $td.find('a').length;
//alert(alength);
var $form = $("body");
//alert($form.find('#history').length);
var a;
if($form.find('#history').length==0){
	a=2;
}
else {
	a=1;
}
//alert(a);
for (a;a<alength+2;a++)
{
a++;
//alert(($("a")[a].onclick));
//$("a")[a-1].href = "#";
//alert($("a")[a-1].onclick);
$("a")[a].onclick = $("a")[a-1].onclick;
$("a")[a].href="#";
}
});
/* Jquery use for Oracle Help URL script End */


var _digits;
var _decimalSep;
var _groupingSep;
function isDigit(
a0
)
{
return(_getDigits()[a0]!=(void 0));
}
function _getDigits()
{
if(_digits==(void 0))
{
var a0=[
0x0030,
0x0660,
0x06F0,
0x0966,
0x09E6,
0x0A66,
0x0AE6,
0x0B66,
0x0BE7,
0x0C66,
0x0CE6,
0x0D66,
0x0E50,
0x0ED0,
0x0F20,
0xFF10
];
_digits=new Object();
for(var a1=0;a1<a0.length;a1++)
{
for(var a2=0;a2<10;a2++)
{
var a3=String.fromCharCode(a0[a1]+a2);
_digits[a3]=a2;
}
}
}
return _digits;
}
function parseDigit(
a0
)
{
var a1=_getDigits()[a0];
if(a1==(void 0))
{
return NaN;
}
else
{
return a1;
}
}
function isNotLowerCase()
{
var a0=alphaChar.charCodeAt(0);
if(a0>0xFF)
{
return true;
}
else
{
return!_isLowerCaseStrict(alphaChar);
}
}
function isLowerCase(
a0
)
{
var a1=a0.charCodeAt(0);
if(a1>0xFF)
{
return!isDigit(a0);
}
else
{
return _isLowerCaseStrict(a0);
}
}
function _isLowerCaseStrict(
a0
)
{
var a1=a0.charCodeAt(0);
return(((a1>=0x61)&&(a1<=0x7A))||
((a1>=0xDF)&&(a1<=0xFF)));
}
function isUpperCase(
a0
)
{
var a1=a0.charCodeAt(0);
if(a1>0xFF)
{
return!isDigit(a0);
}
else
{
return _isUpperCaseStrict(a0);
}
}
function isNotUpperCase(
a0
)
{
var a1=a0.charCodeAt(0);
if(a1>0xFF)
{
return true;
}
else
{
return!_isUpperCaseStrict(a0);
}
}
function _isUpperCaseStrict(
a0
)
{
var a1=a0.charCodeAt(0);
return(((a1>=0x41)&&(a1<=0x5A))||
((a1>=0xC0)&&(a1<=0xDe)));
}
function isLetter(
a0
)
{
return isLowerCase(a0)|isUpperCase(a0);
}
function getUserLanguage()
{
var a0=_locale;
if(a0==(void 0))
{
a0=window.navigator.userLanguage;
if(a0==(void 0))
{
a0=window.navigator.language;
}
}
return a0;
}
function getJavaLanguage(
a0
)
{
if(a0==(void 0))
{
a0=getUserLanguage();
}
var a1=a0.indexOf("-",0);
if(a1==-1)
return a0;
var a2=a0.length;
var a3=a0.substring(0,a1);
a3+="_";
a1++;
var a4=a0.indexOf("-",a1);
if(a4==-1)
{
a4=a2;
}
var a5=a0.substring(a1,
a4);
a3+=a5.toUpperCase();
if(a4!=a2)
{
a3+="_";
a3+=a0.substring(a4+1,
a2);
}
return a3;
}
function getLocaleSymbols(
a0
)
{
var a1=getJavaLanguage(a0);
while(true)
{
var a2=window["LocaleSymbols_"+a1];
if(a2!=(void 0))
{
return a2;
}
else
{
var a3=a1.lastIndexOf("_");
if(a3!=-1)
{
a1=a1.substring(0,a3);
}
else
{
break;
}
}
}
}
function _getEras()
{
return this.getLocaleElements()["Eras"];
}
function _getMonths()
{
return this.getLocaleElements()["MonthNames"];
}
function _getShortMonths()
{
return this.getLocaleElements()["MonthAbbreviations"];
}
function _getWeekdays()
{
return this.getLocaleElements()["DayNames"];
}
function _getShortWeekdays()
{
return this.getLocaleElements()["DayAbbreviations"];
}
function _getAmPmStrings()
{
return this.getLocaleElements()["AmPmMarkers"];
}
function _getZoneStrings()
{
return this.getLocaleElements()["zoneStrings"];
}
function _getLocalPatternChars()
{
return this.getLocaleElements()["localPatternChars"];
}
function _getDecimalSeparator()
{
if(_decimalSep!=(void 0))
return _decimalSep;
return this.getLocaleElements()["NumberElements"][0];
}
function _getGroupingSeparator()
{
if(_groupingSep!=(void 0))
return _groupingSep;
return this.getLocaleElements()["NumberElements"][1];
}
function _getPatternSeparator()
{
return this.getLocaleElements()["NumberElements"][2];
}
function _getPercent()
{
return this.getLocaleElements()["NumberElements"][3];
}
function _getZeroDigit()
{
return this.getLocaleElements()["NumberElements"][4];
}
function _getDigit()
{
return this.getLocaleElements()["NumberElements"][5];
}
function _getMinusSign()
{
return this.getLocaleElements()["NumberElements"][6];
}
function _getExponential()
{
return this.getLocaleElements()["NumberElements"][7];
}
function _getPerMill()
{
return this.getLocaleElements()["NumberElements"][8];
}
function _getInfinity()
{
return this.getLocaleElements()["NumberElements"][9];
}
function _getNaN()
{
return this.getLocaleElements()["NumberElements"][10];
}
function _getCurrencySymbol()
{
return this.getLocaleElements()["CurrencyElements"][0];
}
function _getInternationalCurrencySymbol()
{
return this.getLocaleElements()["CurrencyElements"][1];
}
function _getMonetaryDecimalSeparator()
{
var a0=this.getLocaleElements()["CurrencyElements"][2];
if(a0.length!=0)
{
return a0;
}
else
{
return this.getDecimalSeparator();
}
}
function _getLocaleElements()
{
return this["LocaleElements"];
}
function _getFullTimePatternString()
{
return this.getLocaleElements()["DateTimePatterns"][0];
}
function _getLongTimePatternString()
{
return this.getLocaleElements()["DateTimePatterns"][1];
}
function _getMediumTimePatternString()
{
return this.getLocaleElements()["DateTimePatterns"][2];
}
function _getShortTimePatternString()
{
return this.getLocaleElements()["DateTimePatterns"][3];
}
function _getFullDatePatternString()
{
return this.getLocaleElements()["DateTimePatterns"][4];
}
function _getLongDatePatternString()
{
return this.getLocaleElements()["DateTimePatterns"][5];
}
function _getMediumDatePatternString()
{
return this.getLocaleElements()["DateTimePatterns"][6];
}
function _getShortDatePatternString()
{
return this.getLocaleElements()["DateTimePatterns"][7];
}
function _getDateTimeFormatString()
{
return this.getLocaleElements()["DateTimePatterns"][8];
}
function LocaleSymbols(
a0
)
{
this["LocaleElements"]=a0;
}
LocaleSymbols.prototype.getFullTimePatternString=_getFullTimePatternString;
LocaleSymbols.prototype.getLongTimePatternString=_getLongTimePatternString;
LocaleSymbols.prototype.getMediumTimePatternString=_getMediumTimePatternString;
LocaleSymbols.prototype.getShortTimePatternString=_getShortTimePatternString;
LocaleSymbols.prototype.getFullDatePatternString=_getFullDatePatternString;
LocaleSymbols.prototype.getLongDatePatternString=_getLongDatePatternString;
LocaleSymbols.prototype.getMediumDatePatternString=_getMediumDatePatternString;
LocaleSymbols.prototype.getShortDatePatternString=_getShortDatePatternString;
LocaleSymbols.prototype.getDateTimeFormatString=_getDateTimeFormatString;
LocaleSymbols.prototype.getEras=_getEras;
LocaleSymbols.prototype.getMonths=_getMonths;
LocaleSymbols.prototype.getShortMonths=_getShortMonths;
LocaleSymbols.prototype.getWeekdays=_getWeekdays;
LocaleSymbols.prototype.getShortWeekdays=_getShortWeekdays;
LocaleSymbols.prototype.getAmPmStrings=_getAmPmStrings;
LocaleSymbols.prototype.getZoneStrings=_getZoneStrings;
LocaleSymbols.prototype.getLocalPatternChars=_getLocalPatternChars;
LocaleSymbols.prototype.getDecimalSeparator=_getDecimalSeparator;
LocaleSymbols.prototype.getGroupingSeparator=_getGroupingSeparator;
LocaleSymbols.prototype.getPatternSeparator=_getPatternSeparator;
LocaleSymbols.prototype.getPercent=_getPercent;
LocaleSymbols.prototype.getZeroDigit=_getZeroDigit;
LocaleSymbols.prototype.getDigit=_getDigit;
LocaleSymbols.prototype.getMinusSign=_getMinusSign;
LocaleSymbols.prototype.getExponential=_getExponential;
LocaleSymbols.prototype.getPerMill=_getPerMill;
LocaleSymbols.prototype.getInfinity=_getInfinity;
LocaleSymbols.prototype.getNaN=_getNaN;
LocaleSymbols.prototype.getCurrencySymbol=_getCurrencySymbol;
LocaleSymbols.prototype.getInternationalCurrencySymbol=_getInternationalCurrencySymbol;
LocaleSymbols.prototype.getMonetaryDecimalSeparator=_getMonetaryDecimalSeparator;
LocaleSymbols.prototype.getLocaleElements=_getLocaleElements;
function _formatValidate(
a0
)
{
var a1=this.parse(a0);
if(a1==(void 0))
{
return"Failed:"+this._class+" for value:"+a0;
}
else if(_instanceof(a1,ParseException))
{
return a1;
}
else
{
return(void 0);
}
}
function Format()
{
this._class="Format";
}
Format.prototype.format=(void 0);
Format.prototype.parse=(void 0);
Format.prototype.validate=_formatValidate;
function ParseException(
a0
)
{
this.parseString=a0;
}
ParseException.prototype.errorOffset=(void 0);
ParseException.prototype.parseString=(void 0);
function _noopFormat(
a0
)
{
return a0;
}
function _cjkParse(
a0
)
{
var a1=0;
var a2=this._length;
while(a1<a0.length)
{
var a3=a0.charCodeAt(a1);
if((a3<0x80)||((0xFF60<a3)&&(a3<0xFFA0)))a2--;
else a2-=2;
if(a2<0)
{
_setFailedPos(a1);
return(void 0);
}
a1++;
}
return a0;
}
function CjkFormat(
a0
)
{
this._class="CjkFormat";
this._length=a0;
}
CjkFormat.prototype=new Format();
CjkFormat.prototype.format=_noopFormat;
CjkFormat.prototype.parse=_cjkParse;
function _utf8Format(
a0
)
{
return a0;
}
function _utf8Parse(
a0
)
{
var a1=0;
var a2=this._length;
while(a1<a0.length)
{
var a3=a0.charCodeAt(a1);
if(a3<0x80)a2--;
else if(a3<0x800)a2-=2;
else
{
if((a3&0xF800)==0xD800)
a2-=2;
else
a2-=3;
}
if(a2<0)
{
_setFailedPos(a1);
return(void 0);
}
a1++;
}
return a0;
}
function Utf8Format(
a0
)
{
this._class="Utf8Format";
this._length=a0;
}
Utf8Format.prototype=new Format();
Utf8Format.prototype.format=_noopFormat;
Utf8Format.prototype.parse=_utf8Parse;
function _sbParse(
a0
)
{
if(this._length<a0.length)
{
_setFailedPos(this._length);
return(void 0);
}
return a0;
}
function SBFormat(
a0
)
{
this._class="SBFormat";
this._length=a0;
}
SBFormat.prototype=new Format();
SBFormat.prototype.format=_noopFormat;
SBFormat.prototype.parse=_sbParse;
function _setFailedPos(a0)
{
window["_failedPos"]=a0;
}
function _getDateFieldFormat(a0)
{
var a1=a0.name;
if(a1&&_dfs)
{
var a2=_dfs[a1];
if(a2)
return new SimpleDateFormat(a2);
}
return new SimpleDateFormat();
}
function _fixDFF(a0)
{
var a1=_getDateFieldFormat(a0);
if(a0.value!="")
{
var a2=a1.parse(a0.value);
if(a2!=(void 0))
a0.value=a1.format(a2);
}
}
function _dfsv(
a0,
a1
)
{
if((a0==(void 0))||(a1==(void 0)))
return;
a1+=_getTimePortion(a0);
var a2=new Date(a1);
var a3=_getDateFieldFormat(a0);
var a4=a0.value;
var a1=a3.format(a2);
if(a0.value!=a1)
{
if(a0.onchange!=(void 0))
{
if(_agent.isIE)
{
a0.onpropertychange=function()
{
var a5=window.event;
if(a5.propertyName=='value')
{
a0.onpropertychange=function(){};
a0.onchange(a5);
}
}
a0.value=a1;
}
else
{
a0.value=a1;
var a5=new Object();
a5.type='change';
a5.target=a0;
a0.onchange(a5);
}
}
else
{
a0.value=a1;
}
}
a0.select();
a0.focus();
}
function _returnCalendarValue(
a0,
a1
)
{
var a2=a0.returnValue;
if(a2!=(void 0))
{
var a3=a0._dateField;
if(a3==(void 0))
{
a3=_savedField1879034;
}
_dfsv(a3,a2);
}
}
function _ldp(
a0,
a1,
a2,
a3,
a4
)
{
var a5=document.forms[a0][a1];
var a6=_dfgv(a5);
if(!a6)
{
a6=new Date();
}
if(!a4)
{
a4=_jspDir+_getQuerySeparator(_jspDir);
a4+="_t=fred&_red=cd";
}
else
{
var a7=a4.lastIndexOf('?');
var a8="";
if(a7==-1)
{
a7=a4.length;
}
else
{
a8=a4.substr(a7+1);
}
var a9=a4.lastIndexOf('/',a7);
var a10=a4.substring(0,a9+1);
a10+=_jspDir+_getQuerySeparator(_jspDir);
a10+=a8;
a10+=_getQuerySeparator(a10);
a10+="_t=fred";
var a11=a4.substring(a9+1,a7);
a4=a10;
a4+="&redirect="+escape(a11);
}
a4+="&value="+a6.getTime();
if(_configName.length>0)
{
a4+="&configName="+escape(_configName);
}
a4+="&loc="+_locale;
if(window["_enc"])
{
a4+="&enc="+_enc;
}
if(window["_contextURI"])
{
a4+="&contextURI="+escape(_contextURI);
}
var a12=-1*a6.getTimezoneOffset();
a4+="&tzOffset="+a12;
if(a2!=(void 0))
{
a4+="&minValue="+a2;
}
if(a3!=(void 0))
{
a4+="&maxValue="+a3;
}
var a13=openWindow(self,
a4,
'uix_2807778',
{width:350,height:370},
true,
void 0,
_returnCalendarValue);
a13._dateField=a5;
_savedField1879034=a5;
}
function _dfgv(a0)
{
if(a0.value!="")
return _getDateFieldFormat(a0).parse(a0.value);
return null;
}
function _getTimePortion(a0)
{
var a1=_dfgv(a0);
if(!a1)
a1=new Date();
var a2=new Date(a1.getFullYear(),
a1.getMonth(),
a1.getDate());
return a1-a2;
}
function _getLocaleTimeZoneDifference()
{
var a0=new Date();
var a1=a0.getTimezoneOffset()*-1;
var a2=0;
if(_uixLocaleTZ)
a2=(_uixLocaleTZ-a1)*60*1000;
return a2;
}
function _dfb(a0,a1,a2)
{
if(a1)
_fixDFF(a0);
}
function _dff(a0,a1)
{
_dfa(a0,a1);
}
function _dfa(a0,a1)
{
if(a1!=(void 0))
{
if(window._calActiveDateFields===(void 0))
window._calActiveDateFields=new Object();
if(typeof(a0)=="string")
{
a0=_getElementById(document,a0);
}
window._calActiveDateFields[a1]=a0;
}
}
function _calsd(a0,a1)
{
if(window._calActiveDateFields!=(void 0))
{
var a2=window._calActiveDateFields[a0];
if(a2)
_dfsv(a2,a1);
}
return false;
}
function _updateCal(a0,a1,a2)
{
a1+=('&scrolledValue='+a0.options[a0.selectedIndex].value);
if(a2)
_firePartialChange(a1);
else
document.location.href=a1;
}
function _doCancel()
{
top.returnValue=(void 0);
top.close();
return false;
}
function _selectDate(a0)
{
var a1=new Date(a0);
var a2=a1.getHours();
if(a2>=12)
{
a2=24;
}
a1.setHours(a2);
top.returnValue=a1.getTime();
top._unloadUIXDialog(window.event);
top.close();return false;
}
var _savedField1879034;
var _LovDA="LovDA";
var _LovDP="LovDP";
var _LovEN="eventNames";
var _LovEV="event";
var _LovFI="fieldId";
var _LovFL="field";
var _LovFR="form";
var _LovHR;
var _LovIV=null;
var _LovLD=0;
var _LovNM=null;
var _LovPT="partialTargets";
var _LovSF=null;
var _LovSR="source";
var _LovST="searchText";
var _LovWN='lovWindow';
function _LovInputVTF(a0,
a1,
a2,
a3,
a4,
a5,
a6)
{
var a7=(a5!=(void 0));
var a8=new Date();
var a9=null;
if(a7)
{
if(_LovLD)
{
var a10=a8-_LovLD;
if((a10>=0)&&(a10<2000))
{
return false;
}
}
_LovLD=a8;
}
else if(_agent.isIE)
{
if(_LovIV==a2.value)
return true;
}
var a11=false;
var a12=true;
if(a2)
{
if(a2.value)
{
a12=(a2.value.search(/\S/)<0);
_LovIV=a2.value;
}
if(a12)
{
if((a3!=(void 0))&&a3)
{
a11=!a7;
}
}
else
{
a11=!a7;
}
if(a11)
{
if(a0)
{
var a13=new Object();
var a14=0;
var a15=new Object();
if(a2.form!=(void 0))
{
a9=a2.form;
if(a9.action!=(void 0))
a14=a9.action;
}
a14=_LovInputDDP(a14,a13);
for(var a16 in a13)
a15[a16]=a13[a16];
if(a2.id)
{
if(a6==(void 0))
a6=a2.id;
a13[_LovSR]=a6;
}
else
a13[_LovSR]=a2.name;
a13[_LovST]=a2.value;
a13[_LovPT]=a0;
var a17;
if(a9!=null)
a17=_lovInputEEN(a9.name);
var a18=_LovInputGPF(a17,'v','lovValidate');
var a19=null;
if(a1)
{
a19=function(a13,preencoded)
{
return a1(a13,a2.id);
}
}
if(_LovInputMPC(a19,a13,a18,false,null))
{
var a20=_LovInputUSF();
for(a16 in a13)
{
if((a15[a16]!=null)
&&(a15[a16]==a13[a16]))
{
delete a13[a16];
}
else
{
if(a15[a16]!=null)
{
delete a15[a16];
}
if(!(a20||a4))
a13[a16]=_LovInputENC(a13[a16]);
}
}
var a21=false;
if(a20&&!a4)
{
var a22=_LovInputUAA(a14,a15,a21);
a13[_getPartialParameter()]=true;
_LovInputSFS(a13,a9,a22);
}
else
{
for(a16 in a15)
a13[a16]=a15[a16];
_delayedEventParams=new Object();
var a23='_lovInputSFE(';
if(a4)
{
a23+="1);";
_delayedEventParams[_LovDP]=a13;
_delayedEventParams[_LovDA]=a9;
}
else
{
a23+="0);";
_delayedEventParams[_LovDA]=_LovInputUAA(a14,a13,
a21);
}
var a24=250;
_setRequestedFocusNode(document,a2.id,true);
window.setTimeout(a23,a24);
}
}
}
}
if(a7)
_LovInputOLW(a0,a5,1);
else
_setNavDirty(window,a2.name);
}
return(a12&&!a7);
}
function _lovInputSFE(a0)
{
if(_delayedEventParams==(void 0))
return;
var a1=_delayedEventParams[_LovDA];
var a2=_delayedEventParams[_LovDP];
_delayedEventParams=new Object();
if((a1==(void 0))||(a1==null))
return;
if(a0)
{
_submitPartialChange(a1,0,a2);
}
else
{
_firePartialChange(a1);
}
_LovIV=null;
return;
}
function _lovInputEEN(a0)
{
return window['_lovEvents_'+a0];
}
function _LovInputOLW(a0,a1,a2)
{
var a3=_getDependent(window,_LovWN);
var a4=a1;
var a5=(a2==1);
if(a4==(void 0))
{
a4=new Object();
}
var a6=_LovInputGPF(a4,'A',false);
var a7=_LovInputGPF(a4,'F',0);
var a8=_LovInputGPF(a4,'N',0);
if(!((a6||a8)&&a7))
return;
var a9=_LovInputGPF(a4,'L',0);
var a10=_LovInputGPF(a4,'E',a9);
var a11=_LovInputGPF(a4,'T',a8);
var a12=_LovInputGPF(a4,'I',0);
var a13=_LovInputGPF(a4,'S',0);
var a14=_LovEV;
var a15=_LovInputGPF(a4,'D',location.href);
var a16;
if(!a6)
{
var a17=document[a7][a11];
if(!a17)
return;
a16=a17.value;
}
else
{
a16=_LovInputGPF(a4,'R','');
}
_LovSF=a13;
_LovNM=new Object();
_LovNM[_LovFR]=a7;
_LovNM[_LovFL]=a11;
if(a10)
_LovNM[_LovSR]=a10;
else
_LovNM[_LovSR]=a8;
if(a9)
_LovNM[_LovFI]=a9;
else
_LovNM[_LovFI]=_LovNM[_LovSR];
_LovNM[_LovPT]=a0;
_LovNM[_LovEN]=_lovInputEEN(a7);
if(a3!=(void 0))
{
return false;
}
var a18=_LovInputGPF(a4,'P',false);
var a19=new Object();
var a20=new Object();
if(a18&&a5)
{
a19[_LovEV]=_LovInputGPF(_LovNM[_LovEN],'p','lovPrepare');
a19[_LovSR]=_LovNM[_LovSR];
a19[_LovST]=a16;
if(a0)
{
a19[_LovPT]=a0;
_submitPartialChange(a7,0,a19);
}
else
{
submitForm(a7,0,a19);
}
}
else
{
_LovInputUUP(_LovSR,_LovNM[_LovFI],a15,a19,false);
_LovInputUUP(_LovST,a16,a15,a19,false);
var a21=_LovInputGPF(_LovNM[_LovEN],'f','lovFilter');
var a22=new Object();
if(a18)
{
_LovInputUUP(_LovEV,a21,a15,a19,false);
}
else
{
_LovInputDDP(a15,a19);
for(var a23 in a19)
{
if((a23!=_LovST)&&(a23!=_LovSR))
a20[a23]=a19[a23];
}
if(!_LovInputMPC(a12,a19,a21,true,a22))
return false;
}
for(var a24 in a19)
{
var a25=a19[a24];
if((a20[a24]==(void 0))
||(a20[a24]!=a25))
{
a22[_LovInputENC(a24)]=_LovInputENC(a25);
}
else
a22[a24]=a25;
}
a19=a22;
_LovHR=location.href;
if(!a18)
{
var a26=new Object();
for(var a24 in a19)
{
var a25=a19[a24];
var a27=!((a24==_LovST)
||((a24==_LovSR)&&(a25==_LovNM[_LovSR]))
||((a24==_LovEV)&&(a25==a21)));
a15=_LovInputUUP(a24,a25,a15,a26,a27);
}
a19=a26;
}
_LovInputOMW(a15,_LovInputWCB,a19);
}
return false;
}
function _LovInputUUP(a0,a1,a2,a3,a4)
{
var a5=new RegExp("[?&]"+a0+"(=|&|$)","i");
var a6=a2.match(a5);
var a7=(a6==null);
if(!a7&&(a6.length==1))
a7=(a6[0].length<1);
if(a7)
{
a3[a0]=a1;
}
else if(a4)
{
a2=_addParameter(a2,a0,a1);
}
return a2;
}
function _LovInputGPF(a0,a1,a2)
{
var a3;
if(a0!=(void 0))
{
a3=a0[a1];
}
if(a3==(void 0))
a3=a2;
return a3;
}
function _LovInputMPC(a0,a1,a2,a3,a4)
{
var a5=_LovEV;
var a6=null;
if(a3)
a6=a1[a5];
delete a1[a5];
if(a0)
{
var a7=a0(a1,a4);
if(!a7)
return false;
}
if(!a1[a5])
{
if(a6)
{
a1[a5]=a6;
}
else
{
a1[a5]=a2;
}
}
return true;
}
function _LovInputOMW(a0,a1,a2)
{
var a3="";
if(_jspDir.search(/^http[s]?:/)<0)
a3+=location.protocol+'//'+location.host;
a3+=_jspDir+_getQuerySeparator(_jspDir)+'_t=fredRC';
if(_enc)
a3+="&enc="+_enc;
a3+='&_minWidth=750&_minHeight=550';
if(_configName)
a3+="&configName="+_configName;
if(_contextURI)
a3+="&contextURI="+_contextURI;
a3+='&redirect=';
if(a0.charAt(0)!='/')
{
var a4=location.pathname;
a0=(a4.substr(0,a4.lastIndexOf('/')+1)
+a0);
}
a3+=_LovInputENC(a0);
for(var a5 in a2)
{
var a6=a2[a5];
a3+="&"+a5+"="+a6;
}
lovw=openWindow(window,a3,_LovWN,{width:750,height:550},
true,'dialog',a1);
lovw._LovSL=false;
lovw._LovSF=_LovSF;
lovw._LovNM=_LovNM;
}
function _LovInputPWP(a0,a1)
{
var a2=window[a0];
if(a2==(void 0))
{
if((a1!=(void 0))&&a1[a0])
{
a2=a1[a0];
}
else if(top[a0])
{
a2=top[a0];
}
else if((a1.opener!=(void 0))&&(a1.opener[a0]))
{
a2=a1.opener[a0];
}
}
return a2;
}
function _LovInputWCB(a0,a1)
{
var a2=true;
if(!a0._LovSL)
{
_LovInputSTC(a0.opener);
return false;
}
_LovNM=_LovInputPWP('_LovNM',a0);
if(_LovNM==null)
return false;
_setNavDirty(a0.opener,_LovNM[_LovFL]);
if(a2)
{
var a3=new Object();
a3[_LovEV]=_LovInputGPF(_LovNM[_LovEN],'u','lovUpdate');
a3[_LovSR]=_LovNM[_LovSR];
var a4=_LovNM[_LovPT];
_setRequestedFocusNode(a0.opener.document,_LovNM[_LovFI],false);
if(a4)
{
a3[_LovPT]=a4;
}
var a5=_LovInputPWP('_lovClose',a0);
var a6=_LovNM[_LovFR];
if((a5!=(void 0))&&(a6!=(void 0)))
{
a5(a6,a3,(a4));
}
else
{
var a7=_LovInputDDP(0,a3);
if(a4)
{
a7=_LovInputUAA(a7,a3,true);
_firePartialChange(a7);
}
else
{
a7=_LovInputUAA(a7,a3,true);
location=a7;
}
}
}
return false;
}
function _LovInputDDP(a0,a1)
{
var a2=a0;
if(!a0)
{
if(_LovHR!=(void 0))
{
a2=_LovHR;
}
else if((location!=(void 0))
&&(location.href!=(void 0)))
{
a2=location.href;
}
else
{
return"#";
}
}
if(a2.charAt(a2.length-1)=='#')
{
a2=a2.substr(0,a2.length-1);
}
var a3=a2;
var a4=a2.indexOf('?');
if(a4>0)
{
a3=a2.substr(0,a4);
var a5=a2.substr(a4+1);
var a6=a5.split('&');
for(var a7=0;a7<a6.length;a7++)
{
var a8=a6[a7].indexOf('=');
if(a8>=0)
{
a1[a6[a7].substr(0,a8)]=a6[a7].substr(a8+1);
}
else
{
a1[a6[a7]]="";
}
}
}
return a3;
}
function _LovInputUAA(a0,a1,a2)
{
var a3=((a0.search('\\\?')>=0)?'&':'?');
var a4=a0;
if(a4)
{
for(var a5 in a1)
{
var a6=a1[a5];
a4+=(a3
+(a2?_LovInputENC(a5):a5)
+'=');
if(a6)
a4+=(a2?_LovInputENC(a6):a6);
a3='&';
}
}
return a4;
}
function _LovInputSTC(a0)
{
var a1=_LovInputPWP('_LovNM',a0);
var a2=null;
var a3=null;
if(a1&&a1[_LovFR]&&a1[_LovFL])
{
if((a0!=null)&&(a0["document"]))
{
a2=a0.document;
a3=a2[a1[_LovFR]][a1[_LovFL]];
}
}
if(a3)
{
_pprFocus(a3,a2);
if(a3["select"]!=null)
a3.select();
}
}
function _LovInputCBF()
{
top.close();
return false;
}
function _LovInputSBF(event)
{
top._LovSL=true;
var nameObj=_LovInputPWP('_LovNM',top);
var opn=top.opener;
_setNavDirty(opn,nameObj[_LovFL]);
var stateCheck=_LovInputPWP('_LovSC',window);
var closeWin=true;
if(stateCheck!=(void 0)&&stateCheck!='')
{
closeWin=eval(stateCheck+'(window, "_LOVResFrm")');
}
var selFunc=_LovInputPWP('_LovSF',top);
if(selFunc)
{
top._LovSL=selFunc(window,
opn.document[nameObj[_LovFR]][nameObj[_LovFL]],
event,opn);
}
if(closeWin)
{
window.onunload=function(){top._unloadUIXDialog(window.event);
top.close();};
}
if(_agent.isNav&&_agent.isSolaris)
{
window.onunload=function(){_LovInputWCB(top,event);};
}
var eventNames=_LovInputPWP('_LovEN',window);
var lovSelect=_LovInputGPF(nameObj[eventNames],'s','lovSelect');
submitForm(0,0,{'event':lovSelect,'source':_LovLI});
}
function _LovInputENC(a0)
{
var a1;
var a2=_agent.isNav||_agent.isMac||_agent.atMost("ie",5.49);
if(!a2)
{
a1=encodeURIComponent(a0);
}
else
{
a1=escape(a0);
}
return a1;
}
function _LovInputQSF(a0,a1)
{
var a2=new TableProxy(a0);
a2.setSelectedRow(a1);
_LovInputSBF();
}
function _LovInputUSF()
{
return _enc.toUpperCase()!="UTF-8";
}
function _LovInputSFS(a0,a1,a2)
{
var a3=window.document;
var a4="_LovInput";
if(a1.id)
a4+=a1.id;
else if(a1.name)
a4+=a1.name;
else
a4+="DummyForm";
var a5=a3.createElement("form");
a5.id=a4;
a5.name=a4;
a5.target=a1.target;
a5.method=a1.method;
if(a2)
a5.action=a2;
else
a5.action=a1.action;
for(var a6 in a0)
{
var a7=a3.createElement("input");
a7.type="hidden";
a7.name=a6;
a7.value=a0[a6];
a5.appendChild(a7);
}
a3.body.appendChild(a5);
var a8="_"+a5.name+"Validater";
var a9=false;
if(window[a8]==(void 0))
{
a9=true;
window[a8]=1;
}
_submitPartialChange(a5,0,a0);
if(a9&&(!_agent.isIE))
delete window[a8];
a3.body.removeChild(a5);
}
function _LovInputSOE(a0,a1,a2,a3)
{
if(_getKC(a0)==13)
{
submitForm(a1,1,{'event':a2,'source':a3});
return false;
}
return true;
}
function _decimalFormat(
a0
)
{
return""+a0;
}
function _decimalParse(
a0
)
{
if(!a0)
return(void 0);
var a1=getLocaleSymbols();
if(a1)
{
var a2=a1.getGroupingSeparator();
if((a0.indexOf(a2)==0)||
(a0.lastIndexOf(a2)==(a0.length-1)))
return(void 0);
var a3=new RegExp("\\"+a2,"g");
a0=a0.replace(a3,"");
var a4=new RegExp("\\"+a1.getDecimalSeparator(),"g");
a0=a0.replace(a4,".");
}
var a5=a0.length-1;
while(a5>=0)
{
if(a0.charAt(a5)!=' ')
break;
a5--;
}
if(a5>=0)
{
if((a0.indexOf('e')<0)&&
(a0.indexOf('E')<0)&&
(((a0*a0)==0)||
((a0/a0)==1)))
{
var a6=parseFloat(a0);
if(!isNaN(a6))
{
var a7=a0.length;
var a8=0;
var a9=a0.lastIndexOf('.');
if(a9!=-1)
{
a7=a9;
a8=a0.length-a9-1;
}
var a10;
if((this._maxValue!=(void 0))&&
(a6>this._maxValue))
{
a10=DecimalFormat.LV;
}
else if((this._minValue!=(void 0))&&
(a6<this._minValue))
{
a10=DecimalFormat.MV;
}
else if((this._maxPrecision!=(void 0))&&
(a7>this._maxPrecision))
{
a10=DecimalFormat.LID;
}
else if((this._maxScale!=(void 0))&&
(a8>this._maxScale))
{
a10=DecimalFormat.LFD;
}
if(a10!=(void 0))
{
var a11=this._messages;
if((a11==(void 0))||
(a11[a10]==(void 0)))
return(void 0);
else
return new ParseException(a11[a10]);
}
return a6;
}
}
}
return(void 0);
}
function DecimalFormat(
a0,
a1,
a2,
a3,
a4)
{
this._messages=a0;
this._maxPrecision=a1;
this._maxScale=a2;
this._maxValue=a3;
this._minValue=a4;
this._class="DecimalFormat";
}
DecimalFormat.prototype=new Format();
DecimalFormat.prototype.format=_decimalFormat;
DecimalFormat.prototype.parse=_decimalParse;
DecimalFormat.LFD='LFD';
DecimalFormat.LID='LID';
DecimalFormat.LV='LV';
DecimalFormat.MV='MV';
function _regExpFormat(
a0
)
{
return a0;
}
function _regExpParse(
a0
)
{
var a1=a0.match(this._pattern);
if((a1!=(void 0))&&(a1[0]==a0))
{
return a0;
}
else
{
return(void 0);
}
}
function RegExpFormat(
a0
)
{
this._class="RegExpFormat";
this._pattern=a0;
}
RegExpFormat.prototype=new Format();
RegExpFormat.prototype.format=_regExpFormat;
RegExpFormat.prototype.parse=_regExpParse;
var _agent=new Object();
var _lastDateSubmitted;
var _lastDateReset=0;
var _lastDateValidated=0;
var _lastValidationFailure=0;
var _pprSubmitCount=0;
var _pprSomeAction=false;
var _pprRequestCount=0;
var _pprUnloaded=false;
var _pprBackRestoreInlineScripts=false;
var _pprSavedCursorFlag=false;
var _pprBlocking=false;
var _pprBlockStartTime=0;
var _pprIframeName="_pprIFrame";
var _pprFirstClickPass=false;
var _pprdivElementName='_pprBlockingDiv';
var _pprBlockingTimeout=null;
var _pprEventElement=null;
var _pprChoiceChanged=false;
_delayedEventParams=new Object();
var _initialFormState;
var _initialFormExclude=new Object();
var _initialFormStateName;
var _navDirty;
var _initialFocusID=null;
var _UixFocusRequestDoc=null;
var _UixFocusRequestID=null;
var _UixFocusRequestNext=false;
var _blockCheckUnloadFromDialog=false;
var _saveForm=null;
var _saveDoValidate=null;
var _saveParameters=null;
var _submitRejected=false;
var _inPartialSubmit=false;
function _atLeast(
a0,
a1
)
{
return(!a0||(a0==_agent.kind))&&
(!a1||(a1<=_agent.version));
}
function _atMost(
a0,
a1
)
{
return(a0==_agent.kind)&&(a1>=_agent.version);
}
function _supportsDOM()
{
var a0=false;
if(_agent.isIE)
{
a0=_agent.version>=5.5;
}
else if(_agent.isNav)
{
a0=false;
}
else if(_agent.isGecko)
{
a0=true;
}
return a0;
}
function _agentInit()
{
var a0=navigator.userAgent.toLowerCase();
var a1=parseFloat(navigator.appVersion);
var a2=false;
var a3=false;
var a4=false;
var a5=false;
var a6=false;
var a7="unknown";
var a8=false;
var a9=false;
var a10=false;
if(a0.indexOf("msie")!=-1)
{
a3=true;
var a11=a0.match(/msie (.*);/);
a1=parseFloat(a11[1]);
a7="ie";
}
else if(a0.indexOf("opera")!=-1)
{
a2=true
a7="opera";
}
else if((a0.indexOf("applewebkit")!=-1)||
(a0.indexOf("safari")!=-1))
{
a6=true
a7="safari";
}
else if(a0.indexOf("gecko/")!=-1)
{
a5=true;
a7="gecko";
a1=1.0;
}
else if((a0.indexOf('mozilla')!=-1)&&
(a0.indexOf('spoofer')==-1)&&
(a0.indexOf('compatible')==-1))
{
if(a1>=5.0)
{
a5=true;
a7="gecko";
a1=1.0;
}
else
{
a4=true;
a7="nn";
}
}
if(a0.indexOf('win')!=-1)
{
a8=true;
}
else if(a0.indexOf('mac')!=-1)
{
a10=true;
}
else if(a0.indexOf('sunos')!=-1)
{
a9=true;
}
_agent.isIE=a3;
_agent.isNav=a4;
_agent.isOpera=a2;
_agent.isGecko=a5;
_agent.isSafari=a6;
_agent.version=a1
_agent.kind=a7;
_agent.isWindows=a8;
_agent.isSolaris=a9;
_agent.isMac=a10;
_agent.atLeast=_atLeast;
_agent.atMost=_atMost;
}
_agentInit();
var _ieFeatures=
{
channelmode:1,
copyhistory:1,
directories:1,
fullscreen:1,
height:1,
location:1,
menubar:1,
resizable:1,
scrollbars:1,
status:1,
titlebar:1,
toolbar:1,
width:1
};
var _nnFeatures=
{
alwayslowered:1,
alwaysraised:1,
copyhistory:1,
dependent:1,
directories:1,
height:1,
hotkeys:1,
innerheight:1,
innerwidth:1,
location:1,
menubar:1,
outerwidth:1,
outerheight:1,
resizable:1,
scrollbars:1,
status:1,
titlebar:1,
toolbar:1,
width:1,
"z-lock":1
}
var _modelessFeatureOverrides=
{
};
var _modalFeatureOverrides=
{
};
var _featureDefaults=
{
document:
{
channelmode:false,
copyhistory:true,
dependent:false,
directories:true,
fullscreen:false,
hotkeys:false,
location:true,
menubar:true,
resizable:true,
scrollbars:true,
status:true,
toolbar:true
},
dialog:
{
channelmode:false,
copyhistory:false,
dependent:true,
directories:false,
fullscreen:false,
hotkeys:true,
location:false,
menubar:false,
resizable:true,
scrollbars:true,
status:true
}
}
var _signedFeatures=
{
alwayslowered:1,
alwaysraised:1,
titlebar:1,
"z-lock":1
};
var _booleanFeatures=
{
alwayslowered:1,
alwaysraised:1,
channelmode:1,
copyhistory:1,
dependent:1,
directories:1,
fullscreen:1,
hotkeys:1,
location:1,
menubar:1,
resizable:1,
scrollbars:1,
status:1,
titlebar:1,
toolbar:1,
"z-lock":1
};
function _getContentWidth(
a0,
a1,
a2
)
{
var a3=a0.childNodes;
var a4=_agent.isGecko;
var a5=(a4)
?"tagName"
:"canHaveHTML"
var a6=0;
for(var a7=0;a7<a3.length;a7++)
{
var a8=a3[a7];
if(a8[a5]&&(a8.offsetWidth>0))
{
var a9=0;
var a10=a8["offsetWidth"];
if(a4)
{
if(a10==a1)
{
a9=_getContentWidth(a8,
a10,
a8.offsetLeft);
}
else
{
a9=a10;
}
}
else
{
a9=a8["clientWidth"];
if(a9==0)
{
a9=_getContentWidth(a8,
a10,
a8.offsetLeft);
}
}
if(a9>a6)
{
a6=a9;
}
}
}
if(a6==0)
a6=a1;
var a11=10;
if(_isLTR()||(a2<=5))
{
a11=2*a2;
}
return a6+a11;
}
function _getTop(
a0
)
{
if(!_agent.isGecko)
{
return top;
}
else
{
var a1=(a0)
?a0.window
:window;
while(a1.parent&&(a1.parent!=a1))
{
a1=a1.parent;
}
return a1;
}
}
function _sizeWin(
a0,
a1,
a2,
a3
)
{
var a4=_agent.isGecko;
var a5=_agent.isIE;
if(!(a4||(a5&&_agent.isWindows)))
return;
var a6=a0.window.document.body;
if(a6)
{
var a7=(!a5&&(a6.scrollWidth>a6.clientWidth))
?a6.scrollWidth
:_getContentWidth(a6,a6.offsetWidth,a6.offsetLeft);
var a8=0;
if(a4)
{
a8=a6.offsetHeight+(window.outerHeight-window.innerHeight);
a8+=30;
a7+=(window.outerWidth-a6.offsetWidth);
}
else
{
a8=a6.scrollHeight+(a6.offsetHeight-a6.clientHeight);
a8+=21;
a7+=a6.offsetWidth-a6.clientWidth+16;
a8+=parseInt(a6.topMargin)+parseInt(a6.bottomMargin);
a7+=parseInt(a6.leftMargin)+parseInt(a6.rightMargin);
}
if(a1)
a7+=a1;
if(a2)
a8+=a2;
if(a3!=(void 0))
{
if(a3['W'])
{
var a9=0+a3['W'];
if(a7<a9)
a7=a9;
}
if(a3['H'])
{
var a10=0+a3['H'];
if(a8<a10)
a8=a10;
}
}
var a11=_getTop(a0);
var a12=a5?0:a11.screen.availLeft;
var a13=a5?0:a11.screen.availTop;
var a14=a11.screen.availHeight*0.95;
var a15=a11.screen.availWidth*0.95;
if(a8>a14)
a8=a14;
if(a7>a15)
a7=a15;
a11.resizeTo(a7,a8);
var a16=a5?a11.screenLeft:a11.screenX;
var a17=a5?a11.screenTop:a11.screenY;
var a18=false;
if((a16+a7)>(a12+a15))
{
a16=(a11.screen.availWidth-a7)/2;
a18=true;
}
if((a17+a8)>(a13+a14))
{
a17=(a11.screen.availHeight-a8)/2;
a18=true;
}
if(a18)
{
a11.moveTo(a16,a17);
}
}
}
function _onModalClickNN(
a0
)
{
if(_getValidModalDependent(self))
{
return false;
}
else
{
self.routeEvent(a0);
return true;
}
}
var _mozClickEH=new Object();
function _onModalClickMoz(
a0
)
{
dump(a0);
}
_mozClickEH["handleEvent"]=_onModalClickMoz;
function _onModalFocus()
{
var a0=self.document.body;
var a1=_getValidModalDependent(self);
var a2=_agent.atLeast("ie",5)&&_agent.isWindows;
if(a1)
{
a1.focus();
if(a2)
{
a0.setCapture();
}
}
else
{
if(a2)
{
a0.onlosecapture=null;
a0.releaseCapture();
}
}
}
function _onModalLoseCapture()
{
var a0=_getValidModalDependent(self);
if(a0)
{
window.setTimeout("_onModalFocus()",1);
}
}
function t(a0,a1)
{
if(_tURL!=void 0)
{
document.write('<img src="'+_tURL+'"');
if(a0!=void 0)
document.write(' width="'+a0+'"');
if(a1!=void 0)
document.write(' height="'+a1+'"');
if(_axm!=void 0)
document.write(' alt=""');
document.write('>');
}
}
function openWindow(
a0,
a1,
a2,
a3,
a4,
a5,
a6
)
{
if(a0)
{
if(a4==(void 0))
a4=false;
if(!a5)
{
a5=(a4)?"dialog":"document";
}
if(!a2)
a2="_blank";
var a7=_featureDefaults[a5];
if(a7==(void 0))
{
a5="document";
a7=_featureDefaults[a5];
}
var a8=(a4)
?_modalFeatureOverrides
:_modelessFeatureOverrides;
var a9=(_agent.isIE)
?_ieFeatures
:_nnFeatures;
var a10=null;
if(a3)
{
a10=new Object();
for(var a11 in a3)
{
a10[a11]=a3[a11];
}
}
var a12="";
for(var a13 in a9)
{
var a14=a8[a13];
if(a14==(void 0))
{
if(a10)
{
a14=a10[a13];
delete a10[a13];
}
if(a14==(void 0))
a14=a7[a13];
}
if(a14!=(void 0))
{
var a15=_booleanFeatures[a13]!=(void 0);
if(a14||!a15)
{
a12+=a13;
if(!a15)
{
a12+="="+a14;
}
a12+=",";
}
}
}
for(var a11 in a10)
{
a12+=a11;
if(a10[a11])
a12+="="+a10[a11];
a12+=",";
}
if(a12.length!=0)
{
a12=a12.substring(0,a12.length-1);
}
if(a6)
{
_setDependent(a0,a2,a6);
}
var a16=_agent.atMost("ie",4.99);
var a17=false;
var a18=a0.document.body;
if(a4&&!a16)
{
if(_agent.atLeast("ie",4))
{
a18.style.filter="alpha(opacity=50)";
a17=true;
}
if(_agent.isNav)
{
a0.captureEvents(Event.CLICK);
a0.onclick=_onModalClickNN;
}
else if(_agent.isGecko)
{
a18.addEventListener(Event.CLICK,_mozClickEH,true);
}
a0.onfocus=_onModalFocus;
}
var a19=a0.open(a1,a2,a12);
if(a4&&(_agent.atLeast("ie",5)&&_agent.isWindows))
{
a18.setCapture();
a18.onlosecapture=_onModalLoseCapture;
}
if(a4&&!a16)
{
_setDependent(a0,"modalWindow",a19);
}
a19.focus();
if(a17)
{
a0.setTimeout("_clearAlphaFilter()",1000);
}
return a19;
}
else
{
return null;
}
}
function _getDependents(
a0,
a1
)
{
var a2;
if(a0)
{
a2=a0["_dependents"];
if(a2==(void 0))
{
if(a1)
{
a2=new Object();
a0["_dependents"]=a2;
}
}
}
return a2;
}
function _getDependent(
a0,
a1
)
{
var a2=_getDependents(a0);
var a3;
if(a2)
{
a3=a2[a1];
}
return a3;
}
function _setDependent(
a0,
a1,
a2
)
{
var a3=_getDependents(a0,true);
if(a3)
{
a3[a1]=a2;
}
}
function _getModalDependent(
a0
)
{
return _getDependent(a0,"modalWindow");
}
function _getValidModalDependent(
a0
)
{
var a1=_getModalDependent(a0);
if(a1)
{
if(a1.closed)
{
_setDependent(a0,"modalWindow",(void 0));
a1=(void 0);
}
}
return a1;
}
function _isModalDependent(
a0,
a1
)
{
return(a1==_getModalDependent(a0));
}
function _clearAlphaFilter()
{
if(_getValidModalDependent(self)!=null)
{
self.setTimeout("_clearAlphaFilter()",1000);
}
else
{
self.document.body.style.filter=null;
}
}
function _unloadUIXDialog(
a0
)
{
_blockCheckUnloadFromDialog=false;
_checkUnload(a0);
_blockCheckUnloadFromDialog=true;
}
function _checkUnload(
a0
)
{
if(_blockCheckUnloadFromDialog)
{
_blockCheckUnloadFromDialog=false;
return;
}
if(_isModalAbandoned())
return;
var a1=_getModalDependent(window);
if(a1!=null)
{
_setModalAbandoned(a1);
a1.close();
}
_pprUnloaded=true;
var a2=_getTop();
if(!a2)
return;
var a3=a2["opener"];
if(!a3)
return;
var a4=_getDependent(a3,self.name);
if(_isModalDependent(a3,self))
{
_setDependent(a3,"modalWindow",(void 0));
a3.onfocus=null;
var a5=a3.document.body;
if(_agent.atLeast("ie",4))
{
if(_agent.atLeast("ie",5)&&_agent.isWindows)
{
a5.onlosecapture=null;
a5.releaseCapture();
}
a5.style.filter=null;
}
if(_agent.isNav)
{
a3.releaseEvents(Event.CLICK);
a3.onclick=null;
}
if(_agent.isGecko)
{
a5.removeEventListener(Event.CLICK,
_mozClickEH,
true);
}
}
if(a4!=(void 0))
{
_setDependent(a3,self.name,(void 0));
if(a0==(void 0))
a0=self.event;
a4(a2,a0);
}
}
function _isModalAbandoned()
{
var a0=_getTop();
return a0._abandoned;
}
function _setModalAbandoned(
a0
)
{
a0._abandoned=true;
}
function _focusChanging()
{
if(_agent.isIE)
{
return(window.event.srcElement!=window.document.activeElement);
}
else
{
return true;
}
}
function _getKeyValueString(
a0,
a1,
a2
)
{
var a3=a0[a1];
if(typeof(a3)=="function")
{
a3="[function]";
}
var a4=(_agent.isGecko)
?((a2+1)%3==0)
?'\n'
:'    '
:'\t';
return a1+':'+a3+a4;
}
function _dump(
a0
)
{
dump(a0,{innerText:1,outerText:1,outerHTML:1,innerHTML:1});
}
function dump(
a0,
a1,
a2
)
{
var a3="";
if(a0)
{
if(!a2)
{
a2=a0["name"];
}
var a4="return _getKeyValueString(target, key, index);";
if(_agent.atLeast("ie",5)||_agent.isGecko)
a4="try{"+a4+"}catch(e){return '';}";
var a5=new Function("target","key","index",a4);
var a6=0;
var a7=new Array();
for(var a8 in a0)
{
if((!a1||!a1[a8])&&!a8.match(/DOM/))
{
a7[a6]=a8;
a6++;
}
}
a7.sort();
for(var a9=0;a9<a7.length;a9++)
{
a3+=a5(a0,a7[a9],a9);
}
}
else
{
a2="(Undefined)";
}
if(a3=="")
{
a3="No properties";
}
alert(a2+":\n"+a3);
}
function _validateForm(
a0
)
{
var a1='_'+a0.name+'Validater';
var a2=window[a1];
if(a2)
return a2(a0);
return false;
}
function _getNextNonCommentSibling(
a0,
a1
)
{
var a2=a0.children;
for(var a3=a1+1;a3<a2.length;a3++)
{
var a4=a2[a3];
if(a4&&(a4.tagName!="!"))
{
return a4;
}
}
return null;
}
function _valField(
formName,
nameInForm
)
{
if(nameInForm)
{
var target=document.forms[formName][nameInForm];
var blurFunc=target.onblur;
if(blurFunc)
{
var valFunc=blurFunc.toString();
var valContents=valFunc.substring(valFunc.indexOf("{")+1,
valFunc.lastIndexOf("}"));
var targetString="document.forms['"+
formName+
"']['"+
nameInForm+
"']";
valContents=valContents.replace(/this/,targetString);
var lastArg=valContents.lastIndexOf(",");
valContents=valContents.substring(0,lastArg)+")";
eval(valContents);
}
}
}
function _validationAlert(a0)
{
_recordValidation(true,0);
alert(a0);
_recordValidation(true,0);
}
function _recordValidation(a0,a1)
{
if(!a1)
a1=new Date();
_lastDateValidated=a1;
if(a0)
_lastValidationFailure=a1;
}
function _recentValidation(a0)
{
var a1=false;
var a2=250;
if(_agent.isMac)
{
a2=600;
}
var a3=new Date();
var a4;
a4=a3-_lastValidationFailure;
if((a4>=0)&&(a4<a2))
{
a1=true;
}
else if(!a0)
{
a4=a3-_lastDateValidated;
if((a4>=0)&&(a4<a2))
{
a1=true;
}
}
return a1;
}
function _validateField(
a0,
a1,
a2,
a3,
a4
)
{
var a5=_agent.isNav;
if(a5&&a4)
{
return;
}
if(a5||_agent.isMac||_agent.isGecko)
{
if(_recentValidation(false))
return;
}
var a6=a3||(_getValue(a0)!="");
if(a6&&!window._validating&&_focusChanging())
{
if(a4)
{
var a7=window.document.activeElement;
if(a7)
{
var a8=a0.parentElement;
if(a8==a7.parentElement)
{
var a9=a8.children;
for(var a10=0;a10<a9.length;a10++)
{
if(a0==a9[a10])
{
a6=(a7!=_getNextNonCommentSibling(a8,a10));
}
}
}
}
}
if(a6)
{
var a11=_getValidationError(a0,a1);
if(a11)
{
var a12=_isShowing(a0);
window._validating=a0;
if(a12)
a0.select();
if(!a5&&a12)
{
a0.focus();
if(window["_failedPos"]!=(void 0))
{
if(a0.createTextRange)
{
var a13=a0.createTextRange();
a13.moveStart("character",window["_failedPos"]);
a13.select();
}
else if(a0.selectionStart!=(void 0))
{
a0.selectionStart=window["_failedPos"];
}
window["_failedPos"]=(void 0);
}
}
var a14=_getErrorString(a0,a2,
a11);
if(a14)
{
_validationAlert(a14);
}
if(a5&&a12)
{
a0.focus();
}
}
}
}
}
function _unvalidateField(
a0
)
{
if(window._validating==a0)
{
window._validating=void 0;
}
}
function submitForm(
a0,
a1,
a2
)
{
var a3=true;
if(_agent.isIE)
{
a3=false;
for(var a4 in _delayedEventParams)
{
a3=true;
break;
}
}
if(a3)
{
_delayedEventParams=new Object();
_delayedEventParams["reset"]=true;
}
if((typeof a0)=="string")
{
a0=document[a0];
}
else if((typeof a0)=="number")
{
a0=document.forms[a0];
}
if(!a0)
return false;
var a5=window["_"+a0.name+"Validater"];
if(a5==(void 0))
{
_saveFormForLaterSubmit(a0,a1,a2);
return false;
}
var a6=new Date();
if(_recentSubmit(a6))
{
if(_pprFirstClickPass&&_pprBlocking)
{
_saveFormForLaterSubmit(a0,a1,a2);
}
return;
}
_submitRejected=false;
_inPartialSubmit=false;
_lastDateSubmitted=a6;
if(a1==(void 0))
a1=true;
var a7=true;
if(a1&&!_validateForm(a0))
a7=false;
var a8=window["_"+a0.name+"_Submit"];
if(a8!=(void 0))
{
var a9=new Function("doValidate",a8);
a0._tempFunc=a9;
var a10=a0._tempFunc(a1);
a0._tempFunc=(void 0);
if(a1&&(a10==false))
{
a7=false;
}
}
if(a7)
{
_resetHiddenValues(a0);
var a11=_supportsDOM();
var a12=new Object();
if(a2)
{
for(var a13 in a2)
{
var a14=a2[a13];
if(a14!=(void 0))
{
var a15=a0[a13];
if(a15)
{
a15.value=a14;
}
else if(a11)
{
var a16=document.createElement("input");
a16.type="hidden";
a16.name=a13;
a16.value=a2[a13];
a0.appendChild(a16);
a12[a13]=a16;
}
}
}
}
a0.submit();
if(!_agent.isIE)
{
for(var a13 in a12)
a0.removeChild(a12[a13]);
}
}
return a7;
}
function _saveFormForLaterSubmit(a0,a1,a2)
{
_saveForm=a0;
_saveDoValidate=a1;
_saveParameters=a2;
if(a0.target==_pprIframeName)
{
_inPartialSubmit=true;
}
_submitRejected=true;
}
function _submitFormCheck()
{
if(_submitRejected)
{
if(_inPartialSubmit)
{
_submitPartialChange(_saveForm,_saveDoValidate,_saveParameters);
_inPartialSubmit=false;
}
else
{
submitForm(_saveForm,_saveDoValidate,_saveParameters);
}
_saveForm=null;
_saveDoValidate=null;
_saveParameters=null;
}
}
function resetForm(
form
)
{
var doReload=false;
if((typeof form)=="string")
{
form=document[form];
}
else if((typeof form)=="number")
{
form=document.forms[form];
}
if(!form)
return false;
var resetCallbacks=window["_"+form.name+"_Reset"];
if(resetCallbacks&&!doReload)
{
for(var i=0;i<resetCallbacks.length;i++)
{
var trueResetCallback=unescape(resetCallbacks[i]);
doReload=(eval(trueResetCallback));
}
}
if(doReload)
{
window.document.location.reload();
}
else
{
form.reset();
}
_lastDateReset=new Date();
return doReload;
}
function _resetHiddenValues(
a0
)
{
var a1=window["_reset"+a0.name+"Names"];
if(a1)
{
for(var a2=0;a2<a1.length;a2++)
{
var a3=a0[a1[a2]];
if(a3)
{
a3.value='';
}
}
}
}
function _getValue(a0)
{
var a1=a0;
var a2=a0.type
if(!a2&&a0.length)
{
for(var a3=0;a3<a0.length;a3++)
{
a2=a0[a3].type;
if(a2!=(void 0))
{
a1=a0[a3];
break;
}
}
}
if(a2=="checkbox")
{
return a0.checked;
}
else if(a2.substring(0,6)=="select")
{
a0=a1;
var a4=a0.selectedIndex;
if(a4!=(void 0)&&
a4!=null&&
a4>=0)
{
var a5=a0.options[a4];
var a6=a5.value;
if(!a6)
{
for(var a3=0;a3<a0.options.length;a3++)
{
if(a0.options[a3].value)
return a6;
}
return a5.text;
}
return a6;
}
return"";
}
else if(a2=="radio")
{
if(a0.length)
{
for(var a3=0;a3<a0.length;a3++)
{
if(a0[a3].type=="radio"&&
a0[a3].checked)
{
return a0[a3].value;
}
}
}
else
{
if(a0.checked)
{
return a0.value;
}
}
return"";
}
else
{
return a0.value;
}
}
function _setSelectIndexById(a0,a1)
{
_getElementById(document,a0).selectedIndex=a1;
}
function _multiValidate(
a0,
a1
)
{
var a2="";
if(a1&&!_recentValidation(true))
{
var a3=_getValidations(a0);
if(a3)
{
var a4=true;
for(var a5=0;a5<a1.length;a5+=4)
{
var a6=a0[a1[a5+1]];
var a7=a6.type;
if(!a7&&a6.length)
{
if(a6[0].type!="radio")
{
a6=a6[0];
}
}
var a8=a1[a5+3];
var a9=_getValue(a6);
if(!(a8&&(a9=="")))
{
var a10=_getValidationError(a6,a1[a5],
a3);
if(a10)
{
if(a4)
{
if(_isShowing(a6))
{
a6.focus();
if((a6.type=="text")
&&(a6["value"]!=(void 0))
&&(a6["value"]!=null)
&&(a6["value"].length>0))
{
if(true!=_delayedEventParams["reset"])
a6.select();
}
}
a4=false;
}
var a11=_getErrorString(a6,
a1[a5+2],
a10);
a2+='\n'+a11;
}
}
}
}
_recordValidation((a2.length>0),0);
}
return a2;
}
function _isShowing(
a0)
{
if(!a0.focus||(a0.type=='hidden'))
return false;
if(_agent.isIE)
{
var a1=a0;
while(a1!=(void 0))
{
computedStyle=a1.currentStyle;
if((computedStyle!=(void 0))&&
((computedStyle["visibility"]=="hidden")||
(computedStyle["display"]=="none")))
{
return false;
}
a1=a1.parentNode;
}
return true;
}
else if(!_agent.isNav&&!_agent.isSafari)
{
var a2=a0.ownerDocument.defaultView.getComputedStyle(a0,null);
return((a2["visibility"]!="hidden")&&
(a2["display"]!="none"));
}
}
function _getID(
a0
)
{
if(!_agent.isNav)
{
var a1=a0.id;
var a2=a0.type;
if(!a2&&a0.length)
a2=a0[0].type;
if(a2=="radio")
{
var a3;
if(a0.length)
{
a3=a0[0].parentNode;
}
else
{
a3=a0.parentNode;
}
a1=a3.id;
}
return a1;
}
else
{
var a4=_getForm(a0);
var a5=window["_"+a4.name+"_NameToID"];
if(a5)
{
var a6=_getName(a0);
return a5[a6];
}
}
}
function _getForm(
a0
)
{
var a1=a0.form;
if(a1==(void 0))
{
var a2=a0.type;
if(!a2&&a0.length)
a2=a0[0].type;
if(a2=="radio"&&a0.length)
{
a1=a0[0].form;
}
}
return a1;
}
function _getName(
a0
)
{
var a1=a0.name;
if(a1==(void 0))
{
var a2=a0.type;
if(!a2&&a0.length)
a2=a0[0].type;
if(a2=="radio"&&a0.length)
{
a1=a0[0].name;
}
}
return a1;
}
function _instanceof(
a0,
a1
)
{
if(a1==(void 0))
return false;
while(typeof(a0)=="object")
{
if(a0.constructor==a1)
return true;
a0=a0.prototype;
}
return false;
}
function _getErrorString(
a0,
a1,
a2
)
{
var a3;
var a4=_getForm(a0);
var a5=_getValue(a0);
if(_instanceof(a2,window["ParseException"]))
{
a3=a2.parseString;
}
else
{
var a6=window["_"+a4.name+"_Formats"];
if(a6)
{
a3=a6[a1];
}
}
if(a3)
{
var a7=window["_"+a4.name+"_Labels"];
var a8;
if(a7)
{
a8=a7[_getID(a0)];
}
var a9=window["_"+a4.name+"_Patterns"];
var a10;
if(a9)
{
a10=a9[_getID(a0)];
}
var a11=_formatErrorString(a3,
{
"value":a5,
"label":a8,
"pattern":a10
});
return a11;
}
}
function _getValidations(
a0
)
{
return window["_"+a0.name+"_Validations"];
}
function _getValidationError(
input,
validationIndex,
validations
)
{
if(!validations)
{
validations=_getValidations(input.form);
}
if(validations)
{
var validator=validations[validationIndex];
if(validator)
{
var trueValidator=validator.replace(/%value%/g,"_getValue(input)");
return(eval(trueValidator));
}
}
return(void 0);
}
function _formatErrorString(
a0,
a1
)
{
var a2=a0;
for(var a3 in a1)
{
var a4=a1[a3];
if(!a4)
{
a4="";
}
var a5="%"+a3+"%";
a2=a2.replace(new RegExp('{'+a3+'}','g'),
a5);
var a6=a2.indexOf(a5);
if(a6>=0)
{
a2=a2.substring(0,a6)+
a4+
a2.substring(a6+a5.length);
}
}
return a2;
}
function _chain(
a0,
a1,
a2,
a3,
a4
)
{
var a5=_callChained(a0,a2,a3);
if(a4&&(a5==false))
return false;
var a6=_callChained(a1,a2,a3);
return!((a5==false)||(a6==false));
}
function _callChained(
a0,
a1,
a2
)
{
if(a0&&(a0.length>0))
{
if(a2==(void 0))
{
a2=a1.window.event;
}
var a3=new Function("event",a0);
a1._tempFunc=a3;
var a4=a1._tempFunc(a2);
a1._tempFunc=(void 0);
return!(a4==false);
}
else
{
return true;
}
}
function _checkLength(a0,a1,a2)
{
elementLength=a0.value.length;
if(elementLength>a1)
{
a0.value=a0.value.substr(0,a1);
return true;
}
if(elementLength<a1)
return true;
if(a2.type=='change')
return true;
if(a2)
{
if((a2.which<32)
||((a2.which==118)&&(a2["ctrlKey"])))
return true;
}
return false;
}
function _getElementById(
a0,
a1
)
{
if((_agent.kind!="ie")||(_agent.version>=5))
{
var a2=a0.getElementById(a1);
if((a2==null)||(a2.id==a1))
return a2;
return _findElementById(a0,a1);
}
else
{
return a0.all[a1];
}
}
function _findElementById(
a0,
a1
)
{
if(a0.id==a1)
return a0;
if(a0.childNodes)
{
var a2=a0.childNodes;
for(var a3=0;a3<a2.length;a3++)
{
var a4=_findElementById(a2.item(a3),a1);
if(a4!=null)
return a4;
}
}
return null;
}
function _getQuerySeparator(a0)
{
var a1=a0.charAt(a0.length-1);
if((a1=='&')||(a1=='?'))
return"";
return(a0.indexOf('?')>=0)?'&':'?';
}
function _addParameter(
a0,
a1,
a2
)
{
var a3=a0.indexOf('?');
if(a3==-1)
{
return a0+'?'+a1+'='+a2;
}
else
{
var a4=a0.indexOf('?'+a1+'=',a3);
if(a4==-1)
a4=a0.indexOf('&'+a1+'=',a3+1);
if(a4==-1)
{
return a0+'&'+a1+'='+a2;
}
else
{
var a5=a4+a1.length+2;
var a6=a0.substring(0,a5);
a6+=a2;
var a7=a0.indexOf('&',a5);
if(a7!=-1)
{
a6+=a0.substring(a7);
}
return a6;
}
}
}
function _addFormParameter(
a0,
a1,
a2
)
{
var a3=new Object();
if(a0)
{
for(var a4 in a0)
a3[a4]=a0[a4];
}
a3[a1]=a2;
return a3;
}
function _firePCUpdateMaster(
a0,
a1,
a2,
a3
)
{
var a4=a1+'_dt';
var a5=window[a4];
if(a5!=a0.id)
{
window[a4]=a0.id;
if(a5)
{
var a6=_getElementById(document,a5);
if(a6)
{
_updateDetailIcon(a6,'/marlin/cabo/images/cache/c-sdtl.gif');
}
}
_updateDetailIcon(a0,'/marlin/cabo/images/cache/c-dtl.gif');
_firePartialChange(a2,a3);
}
}
function _updateDetailIcon(
a0,
a1
)
{
a0.firstChild.src=a1;
}
function _firePartialChange(a0)
{
var a1=_addParameter(a0,
_getPartialParameter(),
"true");
var a2=_getElementById(document,_pprIframeName);
_pprRequestCount++;
_pprStartBlocking(window);
if(_agent.isIE)
{
a2.contentWindow.location.replace(a1);
}
else
{
a2.contentDocument.location.replace(a1);
}
}
function _submitPartialChange(
a0,
a1,
a2
)
{
if((typeof a0)=="string")
a0=document[a0];
if(!a0)
return false;
a2=_addFormParameter(a2,_getPartialParameter(),"true");
var a3=a0.target;
a0.target=_pprIframeName;
_pprRequestCount++;
var a4=0;
if((!_agent.isIE)||parent._pprSomeAction)
{
a4=1;
}
_pprSubmitCount+=a4;
parent._pprSomeAction=true;
_pprStartBlocking(window);
var a5=submitForm(a0,a1,a2);
if(!a5)
{
_pprStopBlocking(window);
_pprRequestCount--;
_pprSubmitCount-=a4;
}
a0.target=a3;
}
function _getPartialParameter()
{
if(window._pprPartialParam)
return window._pprPartialParam;
return"partial";
}
function _setOuterHTML(
a0,
a1,
a2
)
{
var a3=a2.tagName;
if(_agent.isIE)
{
var a4=true;
var a5=((a3=="TD")||
(a3=="TH")||
(a3=="CAPTION"));
var a6=!a5&&
((a3=="COL")||
(a3=="COLGROUP")||
(a3=="TR")||
(a3=="TFOOT")||
(a3=="THEAD")||
(a3=="TBODY"));
if(a5||a6)
{
var a7=a0.createElement(a3);
a7.mergeAttributes(a2,false);
if(a5)
{
a7.innerHTML=a2.innerHTML;
}
else
{
if(a6)
{
var a8=a2.firstChild;
while(a8!=null)
{
while(a8!=null&&a8.tagName=="!")
{
a8=a8.nextSibling;
}
a7.appendChild(_setOuterHTML(a0,
null,
a8));
a8=a8.nextSibling;
}
}
}
if(a1)
{
a1.parentNode.replaceChild(a7,a1);
}
else
{
a1=a7;
}
a4=false;
}
if(a4)
{
a1.outerHTML=a2.outerHTML;
}
}
else
{
var a7;
if(a3!='TR')
{
a7=a0.createElement(a3);
if(a3=='SELECT')
{
if(a2.multiple)
{
a7.multiple=a2.multiple;
}
for(var a9=0;a9<a2.options.length;a9++)
{
var a10=a2.options[a9];
var a11=new Option();
a11.value=a10.value;
a11.text=a10.text;
a11.selected=a10.selected;
a7.options[a9]=a11;
}
}
else
{
var a12=a2.innerHTML;
if((a12!=null)&&(a12.length>0))
{
a7.innerHTML=a2.innerHTML;
}
}
var a13=a2.attributes;
for(var a9=0;a9<a13.length;a9++)
{
a7.setAttribute(a13[a9].name,a13[a9].value);
}
}
else
{
a7=a0.importNode(a2,true);
}
a1.parentNode.insertBefore(a7,a1);
a1.parentNode.removeChild(a1);
}
return a1;
}
function _partialUnload()
{
if((parent._pprRequestCount<=0)&&!parent._pprUnloaded)
{
_pprStopBlocking(parent);
if(!(_agent.isIE)&&(parent.document.referrer!=null))
{
parent.history.go(parent.document.referrer);
}
else
{
var a0=-1;
if(_agent.isIE)
{
if(parent._pprSomeAction)
{
a0=-(parent._pprSubmitCount);
}
}
else if(parent._pprSubmitCount&&(parent._pprSubmitCount>0))
{
a0-=parent._pprSubmitCount;
}
parent._pprSubmitCount=0;
parent._pprSomeAction=false;
if(a0<0)
{
parent.history.go(a0);
}
}
}
}
function _partialRedirect(a0)
{
if(a0&&(parent._pprRequestCount>0))
{
if(((typeof a0)=="string")&&(a0.length>0))
{
parent._pprRequestCount--;
parent._pprSubmitCount=0;
parent._pprSomeAction=false;
parent.location.href=a0;
_pprStopBlocking(parent);
}
}
}
function _pprLibraryStore(a0)
{
this.loadedStatus=new Array(a0);
for(var a1=0;a1<a0;a1++)
this.loadedStatus[a1]=false;
this.total=a0;
this.allLibraries=new Array(a0);
}
var _pprLibStore;
function _pprExecScript(a0,a1)
{
if(_pprLibStore&&_pprLibStore.allLibraries!=(void 0))
{
_pprLibStore.allLibraries[a0]=a1;
_pprLibStore.loadedStatus[a0]=true;
for(var a0=_pprLibStore.total-1;a0>=0;a0--)
{
if(!_pprLibStore.loadedStatus[a0])
return;
}
for(var a2=0;a2<_pprLibStore.total;a2++)
{
var a3=parent;
if("_pprIFrame"!=window.name)
{
a3=window;
}
a3.execScript(_pprLibStore.allLibraries[a2]);
}
_pprLibStore=null;
}
}
function _createToLoadArray()
{
var a0=new Array();
var a1=0;
if(window["_pprLibraries"]!=(void 0))
{
for(var a2=0;a2<_pprLibraries.length;a2++)
{
if((parent._cachedLibs==null)
||(parent._cachedLibs.indexOf(_pprLibraries[a2])==-1))
{
a0[a1++]=_pprLibraries[a2];
}
}
}
return a0;
}
function _addLibraryToCache(a0)
{
if((a0.indexOf("ScriptEval"))==-1)
{
if(parent._cachedLibs==null)
parent._cachedLibs=""+a0;
else
parent._cachedLibs+=","+a0;
}
}
function _loadScriptLibrariesIE(a0,a1)
{
if(a1==null)return;
var a2=_getElementById(a0,"_uixDownload");
if(a2==null)return;
var a3=a1.length;
_pprLibStore=new _pprLibraryStore(a3);
for(var a4=0;a4<a3;a4++)
{
var a5="_pprExecScript("+a4+", s);"
var a6=new Function("s",a5);
a2.startDownload(a1[a4],a6);
_addLibraryToCache(a1[a4]);
}
}
function _loadScriptLibrariesGecko(a0,a1)
{
var a2=_getElementById(a0,_pprIframeName);
if(a2)
{
for(var a3=0;(a3<a1.length);a3++)
{
var a4=a0.createElement("script");
a4.setAttribute('src',a1[a3]);
a2.parentNode.insertBefore(a4,a2);
_addLibraryToCache(a1[a3]);
}
}
}
function _loadScriptLibraries(a0)
{
if(window["_pprLibraries"]!=(void 0))
{
var a1=_createToLoadArray();
if(a1.length>0)
{
if(_agent.isIE)
{
_loadScriptLibrariesIE(a0,a1);
}
else
{
_loadScriptLibrariesGecko(a0,a1);
}
}
}
}
function _pprCopyObjectElement(a0,a1)
{
var a2=0;
while(true)
{
var a3="_pprObjectScript"+a2;
var a4=_getElementById(a0,
a3);
if(a4==null)
break;
else
{
var a5=_getCommentedScript(a0,
a3);
}
if(a5!=null)
{
var a6="_pprObjectSpan"+a2;
var a7=_getElementById(a1,
a6);
if(a7!=null)
a7.outerHTML=a5;
}
a2++;
}
}
function _partialChange(a0)
{
if(parent._pprRequestCount<=0)
return;
parent._pprRequestCount--;
parent._pprSomeAction=true;
if(a0)
_fixAllLinks(a0,parent);
var a1=document;
var a2=parent.document;
var a3=_getParentActiveElement();
var a4=null;
var a5=false;
for(var a6=0;a6<_pprTargets.length;a6++)
{
var a7=_pprTargets[a6];
var a8=_getElementById(a1,a7);
var a9=_getElementById(a2,a7);
if(a8&&a9)
{
var a10=_isDescendent(a3,a9);
_setOuterHTML(a2,a9,a8);
if((a10)&&(a4==null))
{
a9=_getElementById(a2,a9.id);
a4=_getNewActiveElement(a2,
a9,
a3);
if(a4==null)
{
a4=_getFirstFocusable(a9);
if(a4!=null)
a5=true;
}
parent._pprEventElement=null;
}
}
}
_pprCopyObjectElement(a1,a2);
_loadScriptLibraries(a2);
_saveScripts(a2);
var a11=_getElementById(a2,"_pprSaveFormAction");
if(a11)
a11.value=document.forms[0].action;
_pprStopBlocking(parent);
var a12=_getRequestedFocusNode();
if(a12!=null)
a4=a12;
_restoreFocus(a4,a5,a2);
_setRequestedFocusNode(null,null,false);
_updateFormActions(a1,a2);
if(_pprFirstClickPass||parent._pprFirstClickPass)
{
_eval(parent,"_submitFormCheck();");
}
}
function _setRequestedFocusNode(a0,a1,a2)
{
top._UixFocusRequestDoc=a0;
top._UixFocusRequestID=a1;
top._UixFocusRequestNext=(a2==true);
}
function _getRequestedFocusNode()
{
if((top._UixFocusRequestDoc!=null)
&&(top._UixFocusRequestID!=null))
{
var a0=_getElementById(top._UixFocusRequestDoc,
top._UixFocusRequestID);
if(!a0)
return null;
if(top._UixFocusRequestNext)
{
for(var a1=a0.nextSibling;
a1!=null;
a1=a1.nextSibling)
{
if(_isFocusable(a1)
||((_agent.isIE)&&(a1.nodeName.toLowerCase()=='a')))
{
a0=a1;
break;
}
}
}
return a0;
}
return null;
}
function _fullChange()
{
if(parent._pprRequestCount>0)
{
parent._pprRequestCount--;
var a0=_getElementById(document,"_pprDisableWrite");
a0.text="var _pprDocumentWrite = document.write;"+
"var _pprDocumentWriteln = document.writeln;"+
"document.write = new Function('return;');"+
"document.writeln = new Function('return;');";
var a1=_getElementById(document,"_pprEnableWrite");
a1.text="document.write = _pprDocumentWrite;"+
"document.writeln = _pprDocumentWriteln";
var a2=document.body;
var a3=a2.getAttribute("onload");
var a4=a2.getAttribute("onunload");
a2.setAttribute("onload",
_getCommentedScript(document,("_pprFullOnload")));
a2.setAttribute("onunload",
_getCommentedScript(document,("_pprFullOnunload")));
var a5=_getDocumentContent();
var a6=
new RegExp("<script id=[\"]*_pprFullChange.*>_fullChange\\(\\)</script>","i");
a5=a5.replace(a6,"");
a2.setAttribute("onload",a3);
a2.setAttribute("onunload",a4);
var a7=parent.document;
if(_agent.isIE)
{
var a8=document.charset;
a7.open();
a7.charset=a8;
}
a7.write(a5);
a7.close();
}
}
function _updateFormActions(a0,a1)
{
var a2=a0.forms;
for(var a3=0;a3<a2.length;a3++)
{
var a4=a2[a3];
if(a4.hasChildNodes())
{
var a5=a4.name;
var a6=a4.action;
var a7=a1.forms[a5];
if(a7)
{
var a8=a7.action;
if(a8!=a6)
a7.action=a6;
}
}
}
}
function _getParentActiveElement()
{
if(parent.document.activeElement)
{
_eval(parent,"_saveActiveElement()");
return parent._pprActiveElement;
}
return null;
}
function _saveActiveElement()
{
if(window._pprEventElement)
window._pprActiveElement=window._pprEventElement;
else if(document.activeElement)
window._pprActiveElement=document.activeElement;
else
window._pprActiveElement=null;
}
function _getNewActiveElement(a0,a1,a2)
{
if(a2.id)
{
var a3=_getElementById(a0,
a2.id);
if(_isFocusable(a3))
return a3;
}
return null;
}
function _getFirstFocusable(a0)
{
if((a0==null)||_isFocusable(a0))
return a0;
if(a0.hasChildNodes)
{
var a1=a0.childNodes;
for(var a2=0;a2<a1.length;a2++)
{
var a3=a1[a2];
var a4=_getFirstFocusable(a3);
if(a4!=null)
return a4;
}
}
return null;
}
function _restoreFocus(a0,a1,a2)
{
if(a0==null)
return;
var a3=_getAncestorByName(a0,"DIV");
if(!a3)
{
_pprFocus(a0,a2);
}
else
{
var a4=a3.scrollTop;
var a5=a3.scrollLeft;
if(((a4==0)&&(a5==0))||!a1)
{
_pprFocus(a0,a2);
}
}
if((_agent.isIE)
&&(a0.tagName=='INPUT')
&&(_getAncestorByName(a0,'TABLE')))
{
_pprFocus(a0,a2);
}
}
function _getAncestorByName(
a0,
a1
)
{
a1=a1.toUpperCase();
while(a0)
{
if(a1==a0.nodeName)
return a0;
a0=a0.parentNode;
}
return null;
}
function _isDescendent(
a0,
a1
)
{
if(a0==null)
return false;
while(a0.parentNode)
{
if(a0==a1)
return true;
a0=a0.parentNode;
}
return false;
}
function _isFocusable(a0)
{
if(a0==null)
return false;
var a1=a0.nodeName.toLowerCase();
if(('a'==a1)&&(a0.href))
{
if(!_agent.isIE||(a0.id))
return true;
var a2=a0.childNodes;
if((a2)&&(a2.length==1))
{
var a3=a2[0].nodeName;
if('img'==a3.toLowerCase())
return false;
}
return true;
}
if(a0.disabled)
return false;
if('input'==a1)
{
return(a0.type!='hidden');
}
return(('select'==a1)||
('button'==a1)||
('textarea'==a1));
}
function _getCommentedScript(a0,a1)
{
var a2=_getElementById(a0,a1);
if(a2!=null)
{
var a3=a2.text;
var a4=0;
var a5=a3.length-1;
while(a4<a5)
{
if(a3.charAt(a4)=='*')
break;
a4++;
}
while(a5>a4)
{
if(a3.charAt(a5)=='*')
break;
a5--;
}
return a3.substring(a4+1,a5);
}
return null;
}
function _eval(targetWindow,code)
{
if(code==null)
return;
if(_agent.isIE)
targetWindow.execScript(code);
else
targetWindow.eval(code);
}
function _getDocumentContent()
{
if(_agent.isIE)
return document.documentElement.outerHTML;
var a0="<html"
var a1=document.documentElement.attributes;
for(var a2=0;a2<a1.length;a2++)
{
a0+=" ";
a0+=a1[a2].name;
a0+="=\""
a0+=a1[a2].value;
a0+="\"";
}
a0+=">";
a0+=document.documentElement.innerHTML;
a0+="</html>";
return a0;
}
function _fixAllLinks(a0,a1,a2)
{
_initialFormState=_getFormState(a0,a2);
_initialFormStateName=a0;
if(a2!=(void 0))
_initialFormExclude=a2;
if(window!=a1)
{
if(a1._initialFormState==null)
a1._initialFormState=new Object();
var a3=_initialFormState;
var a4=a1._initialFormState;
for(var a5 in a3)
a4[a5]=a3[a5];
}
var a6=document.links;
var a7=a1.location.href+'#';
var a8=location.href+'#';
for(var a9=0;a9<a6.length;a9++)
{
var a10=a6[a9].href;
if(!a10
||(a10.substr(0,a8.length)==a8)
||(a10.substr(0,a7.length)==a7)
||(a10.substr(0,11).toLowerCase()=="javascript:")
||(a10.substr(0,7).toLowerCase()=="mailto:")
||(a10.indexOf("_noSv=M")>=0))
{
continue;
}
if(a6[a9].target)
{
continue;
}
var a11=a10.split("'");
a10=a11[0];
for(var a12=1;a12<a11.length;a12++)
a10=a10+"\\'"+a11[a12];
if(!_agent.isNav)
a10=escape(a10);
a6[a9].href="javascript:_submitNav('"+a0+"','"+a10+"')";
}
}
function _isInExclude(a0,a1)
{
if(a0!=(void 0))
{
if(a0[a1]!=(void 0))
return true;
var a2=a1.lastIndexOf(':');
if(a2<0)
return false;
return _isInExclude(a0,a1.substring(0,a2));
}
return false;
}
function _getFormState(a0,a1)
{
var a2=new Object();
var a3=document[a0];
for(var a4=0;a4<a3.length;a4++)
{
var a5=a3.elements[a4].name;
if(a5)
{
var a6=a3[a5];
if(a6)
{
if((a1!=(void 0))&&_isInExclude(a1,a5))
continue;
if(!a6.type||(a6.type!='hidden'))
a2[a5]=_getValue(a6);
}
}
}
return a2;
}
function isNavDirty()
{
var a0=false;
if(_navDirty)
a0=true;
else
{
var a1=_getFormState(_initialFormStateName,_initialFormExclude);
for(var a2 in a1)
{
if(a1[a2]!=_initialFormState[a2])
{
a0=true;
break;
}
}
}
return a0;
}
function _addNavExclude(a0)
{
if(_initialFormExclude!=(void 0))
_initialFormExclude[a0]=1;
else
{
_initialFormExclude=new Object();
_initialFormExclude[a0]=1;
}
}
function _submitNav(a0,a1)
{
if(isNavDirty())
{
var a2=window["_onNavigate"];
if((a2==(void 0))||!(a2(a0,a1)==false))
{
var a3=window['_navEvent'];
if(a3==(void 0))
a3='navigate';
submitForm(a0,0,{'event':a3,'uri':a1});
}
}
else
document.location.href=a1;
}
function _getInputField(a0)
{
var a1=(void 0);
var a2=(void 0);
if(window.event)
{
kc=window.event.keyCode;
a2=window.event.srcElement;
}
else if(a0)
{
kc=a0.which;
a2=a0.target;
}
if(a2!=(void 0)
&&(a2.tagName=="INPUT"||
a2.tagName=="TEXTAREA"))
a1=a2;
return a1;
}
function _enterField(
a0
)
{
var a1;
var a2;
var a3=true;
var a1=_getInputField(a0);
if(a1!=(void 0))
{
a1.form._mayResetByInput=false;
if(a1!=window._validating)
{
a1._validValue=a1.value;
}
a3=false;
}
return a3;
}
function _resetOnEscape(a0)
{
var a1;
var a2=_getInputField(a0);
if(a2!=(void 0))
{
var a3=a2.form;
if(a1==27)
{
var a4=false;
if((a2.selectionStart!=(void 0))&&
(a2.selectionEnd!=(void 0)))
{
a4=(a2.selectionStart!=a2.selectionEnd);
}
else if(document.selection)
{
a4=(document.selection.createRange().text.length!=0);
}
if(!a4)
{
a2.value=a2._validValue;
if(a3._mayResetByInput==true)
{
a3.reset();
a3._mayResetByInput=false;
}
else
{
a3._mayResetByInput=true;
}
}
return false;
}
else
{
a3._mayResetByInput=false;
}
}
return true;
}
function _checkLoad(
a0,
a1,
a2
)
{
if(_agent.isIE)
{
var a3=_getElementById(document,_pprdivElementName);
if(a3&&a3.focus)
a3.focus();
}
restorePartialPageState();
for(var a4=0;a4<document.forms.length;a4++)
{
var a5=document.forms[a4];
if(a5.addEventListener)
{
a5.addEventListener('focus',_enterField,true);
a5.addEventListener('keydown',_resetOnEscape,true);
}
else if(a5.attachEvent)
{
a5.attachEvent('onfocusin',_enterField);
a5.attachEvent('onkeydown',_resetOnEscape);
}
}
if(a1!=(void 0))
{
var a6;
if(_initialFormExclude!=(void 0))
a6=_initialFormExclude;
else
a6=new Object();
if(a2!=(void 0))
{
for(var a7=0;a7<a2.length;a7++)
a6[a2[a7]]=1;
}
_fixAllLinks(a1,window,a6);
}
if((self!=top)&&top["_blockReload"])
{
if((_agent.isIE)
&&(document.onkeydown!=null)
&&(((document.onkeydown).toString().indexOf('_monitor'))>0))
{
document.onkeydown=_monitorNoReload;
}
else
{
document.onkeydown=_noReload;
}
}
if((!_agent.isNav)&&(_initialFocusID!=null))
{
var a8=_getElementById(document,_initialFocusID);
if(a8&&a8.focus)
{
a8.focus();
if(a8.type=='text')
a8.select();
}
}
if(!_agent.isNav)
_loadScriptLibraries(document);
}
function _noReload(a0)
{
if(!a0)a0=window.event;
var a1=a0.keyCode;
if((a1==116)||(a1==82&&a0.ctrlKey))
{
if(a0.preventDefault)a0.preventDefault();
a0.keyCode=0;
return false;
}
}
function _monitorNoReload(a0)
{
if(_agent.isIE)
_monitor(a0);
return _noReload(a0);
}
function _handleClientEvent(a0,a1,a2,a3)
{
var a4=new Object();
a4.type=a0;
a4.source=a1;
a4.params=a2;
var a5=new Function("event",a3);
return a5(a4);
}
function _getCookie(a0)
{
var a1=document.cookie;
var a2="";
var a3=a0+"=";
if(a1)
{
var a4=a1.indexOf("; "+a3);
if(a4<0)
{
a4=a1.indexOf(a3);
if(a4>0)
a4=-1;
}
else
a4+=2;
if(a4>=0)
{
var a5=a1.indexOf(";",a4);
if(a5<0)
a5=a1.length;
a2=unescape(a1.substring(a4+a0.length+1,a5));
}
}
return a2;
}
function _setCookie(a0,a1)
{
var a2=window.location.host;
var a3=a2.indexOf(":");
if(a3>=0)
a2=a2.substr(0,a3);
var a4=new Date();
a4.setFullYear(a4.getFullYear()+10);
var a5=a0+"="+a1+
"; path=/;domain="+a2+"; expires="+a4.toGMTString();
document.cookie=a5;
}
function _setUIXCookie(a0,a1)
{
var a2=_getUIXCookie();
a2[a0]=a1;
var a3=a2[0];
for(var a4=1;a4<a2.length;a4++)
{
a3=a3+"^"+a2[a4];
}
_setCookie("oracle.uix",a3);
}
function _getUIXCookie()
{
var a0=_getCookie("oracle.uix");
var a1;
if(a0)
a1=a0.split("^");
else
a1=new Array("0","","");
return a1;
}
function _defaultTZ()
{
var a0=_getUIXCookie()[2];
if(a0&&(a0.indexOf("GMT")!=0))
{
return;
}
_setUIXCookie(2,_getTimeZoneID());
}
function _getTimeZoneID()
{
var a0=-(new Date()).getTimezoneOffset();
var a1;
if(a0>0)
a1="GMT+";
else
{
a1="GMT-";
a0=-a0;
}
var a2=""+a0%60;
if(a2.length==1)
a2="0"+a2;
return(a1+(Math.floor(a0/60))+":"+a2);
}
function _monitor(a0)
{
var a1=window.event;
if((a1.altKey==true)&&(a1.ctrlKey==false)&&
(a1.keyCode!=null)&&(a1.keyCode!=18)
&&(!a1.repeat))
{
var a2=String.fromCharCode(window.event.keyCode);
var a3=_getNodeWithAccessKey(document,a2);
if(a3!=null&&(a3.getAttribute("uixbtn")!=null))
{
if(a3.htmlFor)
{
var a4=a3.htmlFor;
a3=(a4!=null)
?window.document.getElementById(a4)
:null;
}
if(a3!=null)
{
a3.focus();
a3.click();
}
}
}
return true;
}
function _getNodeWithAccessKey(a0,a1)
{
var a2=a1.toUpperCase();
var a3=a1.toLowerCase();
var a4=
{
activeFound:false,
firstAccessKeyNode:null,
accessKeyNode:null
}
a4=_findAccessKey(document,
a4,
a2,
a3);
var a5=a4.accessKeyNode;
var a6=a4.firstAccessKeyNode;
if((a5==null)&&(a6!=null))
{
a5=a6;
}
return a5;
}
function _findAccessKey(a0,a1,a2,a3)
{
if(a0.nodeType==1)
{
if((a0.accessKey==a2)||
(a0.accessKey==a3))
{
if(a1.activeFound==true)
{
a1.accessKeyNode=a0;
return a1;
}
else if(a1.firstAccessKeyNode==null)
{
a1.firstAccessKeyNode=a0;
}
}
if(a0==document.activeElement)
{
a1.activeFound=true;
}
}
var a4=a0.childNodes;
for(var a5=0;a5<a4.length;a5++)
{
var a1=
_findAccessKey(a4[a5],
a1,
a2,
a3);
if(a1.accessKeyNode!=null)
{
return a1;
}
}
return a1;
}
function _isEmpty(a0)
{
var a1=""+a0;
var a2=0;
while(a2<a1.length)
{
if(a1.charAt(a2)!=' ')
return false;
a2++;
}
return true;
}
function _isLTR()
{
return document.documentElement["dir"].toUpperCase()=="LTR";
}
function _pprConsumeFirstClick(a0)
{
if(_agent.isIE)
{
_pprControlCapture(window,true);
window.document.detachEvent('onclick',_pprConsumeFirstClick);
}
return false;
}
function _pprControlCapture(a0,a1)
{
if(_agent.isIE)
{
var a2=a0.document;
var a3=a2.body;
var a4=_getElementById(a2,_pprdivElementName);
if(a4)
{
if(a1)
{
a4.setCapture();
if(a0._pprEventElement)
a4.focus();
a0._pprSavedCursor=a3.style.cursor;
a3.style.cursor="wait";
a0._pprSavedCursorFlag=true;
}
else if(a0._pprSavedCursorFlag)
{
a4.releaseCapture();
if(a0._pprEventElement)
a0._pprEventElement.focus();
a3.style.cursor=a0._pprSavedCursor;
a0._pprSavedCursor=null;
a0._pprSavedCursorFlag=false;
}
}
}
return;
}
function _pprConsumeBlockedEvent(a0)
{
var a1=true;
if(_pprBlocking)
{
var a2=true;
if(window._pprFirstClickPass)
{
var a3=new Date();
var a4=a3-_pprBlockStartTime;
var a5=150;
if((a4<a5)&&(a0.type=='click'))
{
var a6=a0.explicitOriginalTarget;
a2=!_isSubmittingElement(a6);
}
}
if(a2)
{
a0.stopPropagation();
a0.preventDefault();
a1=false;
}
}
return a1;
}
function _isSubmittingElement(a0)
{
var a1=false;
var a2=a0.nodeName.toUpperCase();
if(a2=="BUTTON")
{
a1=true;
}
else if(a2=="IMG")
{
var a3=a0.parentNode;
var a4=a3.nodeName.toUpperCase();
if(('A'==a4)&&(a3.href))
{
var a5=""+a3["onclick"];
if((a5!=(void 0))&&(a5!=null))
{
a1=((a5.indexOf("submitForm")>0)
||(a5.indexOf("_uixspu")>0)
||(a5.indexOf("_addRowSubmit")>0));
}
}
}
return a1;
}
function _pprConsumeClick(a0)
{
if(_agent.isIE)
{
var a1=document.body;
if((a0.x<a1.offsetLeft)||(a0.y<a1.offsetTop)
||(a0.x>a1.offsetWidth)||(a0.y>a1.offsetHeight))
{
_pprStopBlocking(window);
}
}
return false;
}
function _pprInstallBlockingHandlers(a0,a1)
{
var a2=a0.document;
if(a2==(void 0))
return;
if(_agent.isIE)
{
var a3=a0._pprConsumeFirstClick;
if(a1)
{
var a4=a0.event;
if(a4!=(void 0))
{
var a5=document.elementFromPoint(a4.x,a4.y);
if(!a0._pprFirstClickPass
||(((a4.type=='change')||(a4.type=='blur'))
&&(a4.srcElement==a5))
||(!_isSubmittingElement(a5)))
{
_pprControlCapture(a0,true);
return;
}
}
a2.attachEvent('onclick',a3);
}
else
{
a2.detachEvent('onclick',a3);
_pprControlCapture(a0,false);
}
}
else
{
var a3=a0._pprConsumeBlockedEvent;
var a6={'click':1,'keyup':1,'keydown':1,'keypress':1};
for(var a7 in a6)
{
if(a1)
a2.addEventListener(a7,a3,true);
else
a2.removeEventListener(a7,a3,true);
}
}
}
function _pprStartBlocking(a0)
{
if(!a0._pprBlocking)
{
var a1=a0.document.body;
a0._pprBlockStartTime=new Date();
if(_agent.isGecko)
{
if(a0._pprBlockingTimeout!=null)
{
a0.clearTimeout(a0._pprBlockingTimeout);
}
a0._pprBlockingTimeout=a0.setTimeout("_pprStopBlocking(window);",
8000);
}
else if(_agent.isIE)
{
_pprEventElement=window.document.activeElement;
}
_pprInstallBlockingHandlers(a0,true);
a0._pprBlocking=true;
}
}
function _pprStopBlocking(a0)
{
var a1=a0.document;
if(a0._pprBlocking)
{
if(_agent.isGecko)
{
if(a0._pprBlockingTimeout!=null)
{
a0.clearTimeout(a0._pprBlockingTimeout);
a0._pprBlockingTimeout==null;
}
}
_pprInstallBlockingHandlers(a0,false);
a0._pprEventElement=null;
a0._pprBlocking=false;
}
a0._pprBlocking=false;
}
function _pprChoiceAction()
{
if(!_agent.isIE)
return true;
var a0=false;
if((!window._pprBlocking)&&(_pprChoiceChanged))
{
_pprChoiceChanged=false;
a0=true;
}
return a0;
}
function _pprChoiceChangeEvent(a0)
{
if(!_agent.isIE)
return true;
if(!window._pprBlocking)
_pprChoiceChanged=true;
return true;
}
function _getKC(a0)
{
if(window.event)
return window.event.keyCode;
else if(a0)
return a0.which;
return-1;
}
function _recentSubmit(a0)
{
if(_lastDateSubmitted)
{
var a1=a0-_lastDateSubmitted;
if((a1>=0)&&(a1<200))
return true;
}
return false;
}
function _recentReset(a0)
{
if(_lastDateReset)
{
var a1=a0-_lastDateReset;
if((a1>=0)&&(a1<200))
return true;
}
return false;
}
function _pprFocus(a0,a1)
{
if(_agent.isIE)
{
var a2=_getElementById(a1,_pprdivElementName);
if((a2)&&(a2["focus"]))
a2.focus();
}
a0.focus();
}
function _savePageStateIE()
{
if(!_agent.isIE)
return;
var a0=_getElementById(document,"_pprPageContent");
if(a0==null)
return;
var a1=_getElementById(document,"_pprSaveLib");
if(!(a1!=null&&a1.value!=""))
{
return;
}
var a2=_getElementById(document,"_pprSavePage");
if(a2==null)
return;
a2.value=a0.outerHTML;
}
function _saveScripts(a0)
{
if(!_agent.isIE)
return;
var a1=_getElementById(a0,"_pprSaveScript");
if(a1!=null)
{
var a2=_getCommentedScript(document,"_pprScripts");
a1.value=
a1.value+" "+a2;
}
var a3=_getElementById(a0,"_pprSaveLib");
if(a3!=null&&(window["_pprLibraries"]!=(void 0)))
{
for(var a4=0;(a4<_pprLibraries.length);a4++)
{
if(a3.value.indexOf(_pprLibraries[a4])==-1)
{
if(a3.value!="")
a3.value+=","+_pprLibraries[a4];
else
a3.value+=_pprLibraries[a4];
}
}
}
}
function restorePartialPageState()
{
if(!_agent.isIE)
return;
var a0=_getElementById(document,"_pprSavePage");
if(a0==null||a0.value=="")
return;
var a1=_getElementById(document,"_pprPageContent");
if(a1==null)
return;
a1.outerHTML=a0.value;
var a2=_getElementById(document,"_pprSaveFormAction");
if(a2==null)
{
_pprBackRestoreInlineScripts=true;
var a3=_getElementById(document,"_pprSaveLib");
if(a3!=null&&a3.value!="")
{
var a4=a3.value.split(",");
_loadScriptLibrariesIE(document,a4);
}
}
else
{
if(a2.value)
document.forms[0].action=a2.value;
submitForm(0,0,{'event':'stateSynch','source':'__unknown__'});
}
}
function _setNavDirty(a0,a1)
{
var a2=a0;
if(a2==(void 0)||!a2)
{
a2=window;
}
var a3=a2._initialFormExclude;
if((a1==(void 0))
||!a1
||!_isInExclude(a3,a1))
{
a2._navDirty=true;
}
}
var _AD_ERA=void 0;
var _dfLenient;
function _getADEra()
{
if(_AD_ERA==(void 0))
{
_AD_ERA=new Date(0);
_AD_ERA.setFullYear(1);
}
return _AD_ERA;
}
function _simpleDateFormat(
a0
)
{
var a1=new Object();
a1.value="";
var a2=this._pattern;
if(typeof a2!="string")
a2=a2[0];
_doClumping(a2,
this._localeSymbols,
_subformat,
a0,
a1);
return a1.value;
}
function _simpleDateParse(
a0
)
{
var a1=this._pattern;
if(typeof a1=="string")
{
return _simpleDateParseImpl(a0,
a1,
this._localeSymbols);
}
else
{
var a2;
for(a2=0;a2<a1.length;a2++)
{
var a3=_simpleDateParseImpl(a0,
a1[a2],
this._localeSymbols);
if(a3!=(void 0))
return a3;
}
}
}
function _simpleDateParseImpl(
a0,
a1,
a2)
{
var a3=new Object();
a3.currIndex=0;
a3.parseString=a0;
a3.parsedHour=(void 0);
a3.parsedMinutes=(void 0);
a3.parsedSeconds=(void 0);
a3.parsedMilliseconds=(void 0);
a3.isPM=false;
a3.parsedBC=false;
a3.parsedFullYear=(void 0);
a3.parsedMonth=(void 0);
a3.parsedDate=(void 0);
a3.parseException=new ParseException();
var a4=new Date(0);
a4.setDate(1);
if(_doClumping(a1,
a2,
_subparse,
a3,
a4))
{
if(a0.length!=a3.currIndex)
{
return(void 0);
}
var a5=a3.parsedFullYear;
if(a5!=(void 0))
{
if(a3.parsedBC)
{
a5=_getADEra().getFullYear()-a5;
}
a4.setFullYear(a5);
a3.parsedFullYear=a5;
}
var a6=a3.parsedMonth;
if(a6!=(void 0))
a4.setMonth(a6);
var a7=a3.parsedDate;
if(a7!=(void 0))
a4.setDate(a7);
var a8=a3.parsedHour;
if(a8!=(void 0))
{
if(a3.isPM&&(a8<12))
{
a8+=12;
}
a4.setHours(a8);
a3.parsedHour=a8;
}
var a9=a3.parsedMinutes;
if(a9!=(void 0))
a4.setMinutes(a9);
var a10=a3.parsedSeconds;
if(a10!=(void 0))
a4.setSeconds(a10);
var a11=a3.parsedMilliseconds;
if(a11!=(void 0))
a4.setMilliseconds(a11);
if(!_isStrict(a3,a4))
{
return(void 0);
}
return a4;
}
else
{
return(void 0);
}
}
function _isStrict(
a0,
a1)
{
var a2=["FullYear","Month","Date","Hours","Minutes",
"Seconds","Milliseconds"];
for(var a3=0;a3<a2.length;a3++)
{
var a4="parsed"+a2[a3];
if(a0[a4]!=(void 0)&&
a0[a4]!=a1["get"+a2[a3]]())
{
return false;
}
}
return true;
}
function _doClumping(
a0,
a1,
a2,
a3,
a4
)
{
var a5=a0.length;
var a6=false;
var a7=0;
var a8=void 0;
var a9=0;
for(var a10=0;a10<a5;a10++)
{
var a11=a0.charAt(a10);
if(a6)
{
if(a11=="\'")
{
a6=false;
if(a7!=1)
{
a9++;
a7--;
}
if(!a2(a0,
a1,
"\'",
a9,
a7,
a3,
a4))
{
return false;
}
a7=0;
a8=void 0;
}
else
{
a7++;
}
}
else
{
if(a11!=a8)
{
if(a7!=0)
{
if(!a2(a0,
a1,
a8,
a9,
a7,
a3,
a4))
{
return false;
}
a7=0;
a8=void 0;
}
if(a11=='\'')
{
a6=true;
}
a9=a10;
a8=a11;
}
a7++;
}
}
if(a7!=0)
{
if(!a2(a0,
a1,
a8,
a9,
a7,
a3,
a4))
{
return false;
}
}
return true;
}
function _subformat(
a0,
a1,
a2,
a3,
a4,
a5,
a6
)
{
var a7=null;
var a8=false;
if((a2>='A')&&(a2<='Z')||
(a2>='a')&&(a2<='z'))
{
switch(a2)
{
case'D':
a7="(Day in Year)";
break;
case'E':
{
var a9=a5.getDay();
a7=(a4<=3)
?a1.getShortWeekdays()[a9]
:a1.getWeekdays()[a9];
}
break;
case'F':
a7="(Day of week in month)";
break;
case'G':
{
var a10=a1.getEras();
a7=(a5.getTime()<_getADEra().getTime())
?a10[0]
:a10[1];
}
break;
case'M':
{
var a11=a5.getMonth();
if(a4<=2)
{
a7=_getPaddedNumber(a11+1,a4);
}
else if(a4==3)
{
a7=a1.getShortMonths()[a11];
}
else
{
a7=a1.getMonths()[a11];
}
}
break;
case'S':
a7=_getPaddedNumber(a5.getMilliseconds(),a4);
break;
case'W':
a7="(Week in Month)";
break;
case'a':
{
var a12=a1.getAmPmStrings();
a7=(_isPM(a5.getHours()))
?a12[1]
:a12[0];
}
break;
case'd':
a7=_getPaddedNumber(a5.getDate(),a4);
break;
case'h':
hours=a5.getHours();
if(_isPM(hours))
hours-=12;
if(hours==0)
hours=12;
a7=_getPaddedNumber(hours,a4);
break;
case'K':
hours=a5.getHours();
if(_isPM(hours))
hours-=12;
a7=_getPaddedNumber(hours,a4);
break;
case'k':
hours=a5.getHours();
if(hours==0)
hours=24;
a7=_getPaddedNumber(hours,a4);
break;
case'H':
a7=_getPaddedNumber(a5.getHours(),a4);
break;
case'm':
a7=_getPaddedNumber(a5.getMinutes(),a4);
break;
case's':
a7=_getPaddedNumber(a5.getSeconds(),a4);
break;
case'w':
a7="(Week in year)";
break;
case'y':
{
var a13=a5.getFullYear();
var a14=(a4<=2)
?a4
:(void 0);
a7=_getPaddedNumber(a13,a4,a14);
}
break;
case'z':
{
var a15=-1*a5.getTimezoneOffset()/60;
a7="GMT";
if(a15>0)a7+="+";
a7+=_getPaddedNumber(a15,2);
}
break;
default:
a7="";
}
}
else
{
a7=a0.substring(a3,a3+a4);
}
a6.value+=a7;
return true;
}
function _getLocaleTimeZoneDifferenceInHours()
{
var a0=new Date();
var a1=a0.getTimezoneOffset()*-1;
var a2=0;
if(_uixLocaleTZ)
a2=(_uixLocaleTZ-a1)/60;
return a2;
}
function _subparse(
a0,
a1,
a2,
a3,
a4,
a5,
a6
)
{
var a7=a5.currIndex;
if((a2>='A')&&(a2<='Z')||
(a2>='a')&&(a2<='z'))
{
switch(a2)
{
case'D':
if(_accumulateNumber(a5,3)==(void 0))
{
return false;
}
break;
case'E':
{
var a8=_matchArray(a5,
(a4<=3)
?a1.getShortWeekdays()
:a1.getWeekdays());
if(a8==(void 0))
{
return false;
}
}
break;
case'F':
if(_accumulateNumber(a5,2)==(void 0))
{
return false;
}
break;
case'G':
{
var a9=_matchArray(a5,a1.getEras());
if(a9!=(void 0))
{
if(a9==0)
{
a5.isBC=true;
}
}
else
{
return false;
}
}
break;
case'M':
{
var a10;
var a11=0;
if(a4<=2)
{
a10=_accumulateNumber(a5,2);
a11=-1;
}
else
{
var a12=(a4==3)
?a1.getShortMonths()
:a1.getMonths();
a10=_matchArray(a5,a12);
}
if(a10!=(void 0))
{
a5.parsedMonth=(a10+a11);
}
else
{
return false;
}
}
break;
case'S':
{
var a13=_accumulateNumber(a5,3);
if(a13!=(void 0))
{
a5.parsedMilliseconds=a13;
}
else
{
return false;
}
}
break;
case'W':
if(_accumulateNumber(a5,2)==(void 0))
{
return false;
}
break;
case'a':
{
var a14=_matchArray(a5,
a1.getAmPmStrings());
if(a14==(void 0))
{
return false;
}
else
{
if(a14==1)
{
a5.isPM=true;
}
}
}
break;
case'd':
{
var a15=_accumulateNumber(a5,2);
if(a15!=(void 0))
{
a5.parsedDate=a15;
}
else
{
return false;
}
}
break;
case'h':
case'k':
case'H':
case'K':
{
var a16=_accumulateNumber(a5,2);
if(a16!=(void 0))
{
if((a2=='h')&&(a16==12))
a16=0;
if((a2=='k')&&(a16==24))
a16=0;
a5.parsedHour=a16;
}
else
{
return false;
}
}
break;
case'm':
{
var a17=_accumulateNumber(a5,2);
if(a17!=(void 0))
{
a5.parsedMinutes=a17;
}
else
{
return false;
}
}
break;
case's':
{
var a18=_accumulateNumber(a5,2);
if(a18!=(void 0))
{
a5.parsedSeconds=a18;
}
else
{
return false;
}
}
break;
case'w':
if(_accumulateNumber(a5,2)==(void 0))
{
return false;
}
break;
case'y':
{
var a19=_accumulateNumber(a5,4);
var a20=a5.currIndex-a7;
if(a19!=(void 0))
{
if((a20>2)&&
(a4<=2)&&
(a19<=999))
{
return false;
}
else if((a4<=2)&&(a19>=0)&&(a19<=100))
{
a19=_fix2DYear(a19);
}
else if(a4==4)
{
if(a20==3)
return false;
if(a20<=2)
a19=_fix2DYear(a19);
}
if(a19==0)
return false;
a5.parsedFullYear=a19;
}
else
{
return false;
}
}
break;
case'z':
{
if(!_matchText(a5,"GMT"))
{
return false;
}
if(_matchArray(a5,["-","+"])==(void 0))
{
return false;
}
if(_accumulateNumber(a5,2)==(void 0))
{
return false;
}
}
break;
default:
}
}
else
{
return _matchText(a5,
a0.substring(a3,a3+a4));
}
return true;
}
function _fix2DYear(a0)
{
var a1;
if(_df2DYS!=(void 0))
{
var a2=_df2DYS;
a1=a2-(a2%100);
a0+=a1;
if(a0<a2)
a0+=100;
}
else
{
var a3=new Date().getFullYear();
a1=a3-(a3%100)-100;
a0+=a1;
if(a0+80<a3)
{
a0+=100;
}
}
return a0;
}
function _matchArray(
a0,
a1
)
{
for(var a2=0;a2<a1.length;a2++)
{
if(_matchText(a0,a1[a2]))
{
return a2;
}
}
return(void 0);
}
function _matchText(
a0,
a1
)
{
if(!a1)
return false;
var a2=a1.length;
var a3=a0.currIndex;
var a4=a0.parseString;
if(a2>a4.length-a3)
{
return false;
}
var a5=a4.substring(a3,a3+a2);
var a6=a5.toLowerCase();
var a7=a1.toLowerCase();
if(a6!=a7)
return false;
a0.currIndex+=a2;
return true;
}
function _accumulateNumber(
a0,
a1
)
{
var a2=a0.currIndex;
var a3=a2;
var a4=a0.parseString;
var a5=a4.length;
if(a5>a3+a1)
a5=a3+a1;
var a6=0;
while(a3<a5)
{
var a7=parseDigit(a4.charAt(a3));
if(!isNaN(a7))
{
a6*=10;
a6+=a7;
a3++;
}
else
{
break;
}
}
if(a2!=a3)
{
a0.currIndex=a3;
return a6;
}
else
{
return(void 0);
}
}
function _isPM(
a0
)
{
return(a0>=12);
}
function _getPaddedNumber(
a0,
a1,
a2
)
{
var a3=a0.toString();
if(a1!=(void 0))
{
var a4=a1-a3.length;
while(a4>0)
{
a3="0"+a3;
a4--;
}
}
if(a2!=(void 0))
{
var a5=a3.length-a2;
if(a5>0)
{
a3=a3.substring(a5,
a5+a2);
}
}
return a3;
}
function SimpleDateFormat(
a0,
a1
)
{
this._class="SimpleDateFormat";
this._localeSymbols=getLocaleSymbols(a1);
if(a0==(void 0))
a0=this._localeSymbols.getShortDatePatternString();
var a2=new Array();
if(a0)
a2=a2.concat(a0);
if(_dfLenient)
{
var a3=a2.length;
for(var a4=0;a4<a3;a4++)
{
if(a2[a4].indexOf('MMM')!=-1)
{
a2[a2.length]=a2[a4].replace(/MMM/g,'MM');
a2[a2.length]=a2[a4].replace(/MMM/g,'M');
}
}
var a3=a2.length;
for(var a4=0;a4<a3;a4++)
{
if(a2[a4].indexOf('/')!=-1)
{
a2[a2.length]=a2[a4].replace(/\//g,'-');
a2[a2.length]=a2[a4].replace(/\//g,'.');
}
if(a2[a4].indexOf('-')!=-1)
{
a2[a2.length]=a2[a4].replace(/-/g,'/');
a2[a2.length]=a2[a4].replace(/-/g,'.');
}
if(a2[a4].indexOf('.')!=-1)
{
a2[a2.length]=a2[a4].replace(/\./g,'-');
a2[a2.length]=a2[a4].replace(/\./g,'/');
}
}
}
this._pattern=a2;
}
SimpleDateFormat.prototype=new Format();
SimpleDateFormat.prototype.format=_simpleDateFormat;
SimpleDateFormat.prototype.parse=_simpleDateParse;
function getTableName(
a0
)
{
var a1=a0.name;
if(a1==(void 0))
return;
var a2=a1.indexOf(":");
if(a2>=0)
return a1.substring(0,a2);
}
function getTableRow(
a0
)
{
var a1=a0.name;
if(a1==(void 0))
return;
var a2=a1.lastIndexOf(":");
if(a2>=0)
{
var a3=a1.substring(a2+1);
return Number(a3);
}
}
function getTableElementName(
a0
)
{
var a1=a0.name;
if(a1==(void 0))
return;
var a2=a1.indexOf(":");
if(a2>=0)
{
var a3=a1.lastIndexOf(":");
if(a3>=0)
{
return a1.substring((a2+1),a3);
}
}
}
function tableSelectAll(
a0
)
{
new TableProxy(a0).selectAll();
}
function tableSelectNone(
a0
)
{
var a1=new TableProxy(a0);
a1.selectNone();
}
function TableProxy(
a0,
a1,
a2
)
{
this.tableName=a0;
var a3=a0+":length";
var a4=document;
if(a2!=(void 0))
{
a4=a2.document;
}
if(a1!=(void 0))
{
this.formName=a1;
}
else
{
var a5=a4.forms.length;
for(var a6=0;a6<a5;a6++)
{
if(a4.forms[a6][a3]!=(void 0))
{
this.formName=a4.forms[a6].name;
break;
}
}
}
this._form=a4.forms[this.formName];
this.length=this._form[a3].value;
}
TableProxy.prototype.getValue=_getTableValue;
TableProxy.prototype.getLength=_getTableLength;
TableProxy.prototype.getSelectedRow=_getSelectedRow;
TableProxy.prototype.getSelectedRows=_getSelectedRows;
TableProxy.prototype.setSelectedRow=_setSelectedRow;
TableProxy.prototype.getFormElement=_getFormElement;
TableProxy.prototype.isMultiSelect=_isMulti;
TableProxy.prototype.selectAll=_selectAll;
TableProxy.prototype.getSelectAll=_getSelectAll;
TableProxy.prototype.selectNone=_selectNone;
TableProxy.prototype.getSelectNone=_getSelectNone;
TableProxy.prototype._multiSelect=_multiSelect;
function _hasSelection()
{
var a0=this._form;
var a1=this.tableName+":selected";
if(a0[a1]!=(void 0))
return true;
a1=this.tableName+":selectMode";
return(a0[a1]!=(void 0));
}
function _getTableValue()
{
var a0="_"+this.tableName+"_value";
if(window[a0]!=(void 0))
return window[a0]-1;
return 0;
}
function _getSelectedRow()
{
if(!(this._hasSelection()))
return-1;
var a0=this._form[this.tableName+":selected"];
if(a0==(void 0))
return-1;
else if(a0.length!=(void 0))
{
var a1;
for(var a2=0;a2<a0.length;a2++)
{
a1=a0[a2];
if((a1!=(void 0))&&a1.checked)
return a2;
}
}
else if(a0.checked)
{
return a0.value;
}
return-1;
}
function _getSelectedRows()
{
if(!(this._hasSelection()))
return-1;
var a0;
var a1=new Array();
var a2=0;
for(var a3=0;a3<this.length;a3++)
{
a0=this.tableName+":selected:"+a3;
a0=this._form[a0];
if((a0!=(void 0))&&a0.checked)
{
a1[a2]=a3;
a2++;
}
}
return a1;
}
function _getTableLength()
{
return this.length;
}
function _getFormElement(
a0,
a1
)
{
var a2=this.tableName+":"+a0+":"+a1;
return this._form[a2];
}
function _isMulti()
{
var a0=this.tableName+":selected:0";
a0=this._form[a0];
return(a0!=(void 0));
}
function _selectAll()
{
this._multiSelect(true);
}
function _getSelectAll()
{
if(!(this._hasSelection()))
return false;
var a0=this.tableName+":selectMode";
var a1=this._form[a0];
if(a1!=(void 0))
{
return a1.value=="all";
}
return false;
}
function _selectNone()
{
this._multiSelect(false);
}
function _getSelectNone()
{
if(!(this._hasSelection()))
return false;
var a0=this.tableName+":selectMode";
var a1=this._form[a0];
if(a1!=(void 0))
{
return a1.value=="none";
}
return false;
}
function _multiSelect(
a0
)
{
if(!(this._hasSelection()))
return;
var a1;
var a2=0;
for(var a3=0;a3<this.length;a3++)
{
a1=this.tableName+":selected:"+a3;
a1=this._form[a1];
if((a1!=(void 0))&&
(!a1.disabled))
{
a1.checked=a0;
}
}
var a4=this.tableName+":selectMode";
var a5=this._form[a4];
if(a5!=(void 0))
{
if(a0)
a5.value="all";
else
a5.value="none";
}
}
function _tableProxyToString()
{
var a0="TableProxy, tableName="+this.tableName+"\n";
a0+="form="+this.formName+"\n";
a0+="_hasSelection="+this._hasSelection()+"\n";
a0+="selectedRow="+this.getSelectedRow()+"\n";
a0+="selectedRows="+this.getSelectedRows()+"\n";
a0+="getLength="+this.getLength()+"\n";
a0+="selectMode=";
var a1=this.tableName+":selectMode";
var a2=this._form[a1];
if(a2!=null)
a0+=a2.value+"\n";
else
a0+="NULL\n";
return a0;
}
function _setSelectedRow(a0)
{
if(a0==(void 0))
return;
var a1=this._form[this.tableName+":selected"];
if(a1==(void 0))
{
a1=this._form[this.tableName+":selected:"+a0];
}
if(a1==(void 0))
{
return;
}
else if(a1.length!=(void 0))
{
for(var a2=0;a2<a1.length;a2++)
{
if((a1[a2]!=(void 0))&&(a1[a2].value==a0))
a1[a2].checked=true;
}
}
else if(a1.value==a0)
{
a1.checked=true;
}
return;
}
TableProxy.prototype._hasSelection=_hasSelection;
TableProxy.prototype.toString=_tableProxyToString;
