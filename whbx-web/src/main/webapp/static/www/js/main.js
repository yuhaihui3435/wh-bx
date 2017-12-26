/*------------------------------------------------------------------
 * Theme Name: Hostio Responsive Template
 * Theme URI: http://www.brandio.io/envato/hostio
 * Author: Brandio
 * Author URI: http://www.brandio.io/
 * Description: A Bootstrap Responsive HTML5 Template
 * Version: 1.0
 * Licensed under MIT (https://github.com/twbs/bootstrap/blob/master/LICENSE)
 * Bootstrap v3.3.6 (http://getbootstrap.com)
 * Copyright 2016 Brandio.
 -------------------------------------------------------------------*/
"use strict";
$(window).on("load", function () {
    
    // Adding hover style for the feature box
    var featureBox = $(".feature-box", "#features");
    featureBox.on("mouseover", function () {
        featureBox.removeClass("active");
        $(this).addClass("active");
        return false;
    });
    // URL Validation
    function ValidURL(str) {
        var pattern = new RegExp('((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.?)+[a-z]{2,}|' + // domain name
                '((\\d{1,3}\\.){3}\\d{1,3}))' + // OR ip (v4) address
                '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + // port and path
                '(\\?[;&a-z\\d%_.~+=-]*)?' + // query string
                '(\\#[-a-z\\d_]*)?$', 'i'); // fragment locator
        if (!pattern.test(str)) {
            return false;
        } else {
            return true;
        }
    }
   
});