/**
 * global Storage for setup params
 * will initialized before the main application
 */
var SETUPS = SETUPS || (//eslint-disable-line
/**
 * @exports setups
 */

function() {
	'use strict';
	var
	//Private
	__ = {},
		//Public API
		exports = {
			__: __
		};
	__.stored = {};
	/**
	 * @param {string} key
	 * @param {*} value
	 */
	exports.set = function(key, value) {
		__.stored[key] = value;
	};

	/**
	 * @param {string} key
	 * @param {*} value
	 * @returns {boolean} removed
	 */
	exports.unset = function(key) {
		if (key in __.stored) {
			delete __.stored[key];
			return true;
		} else {
			return false;
		}
	};
	/**
	 * @param {string} key
	 * @returns {*} - stored value
	 */
	exports.get = function(key) {
		return __.stored[key] || null;
	};
	return exports;
})();
