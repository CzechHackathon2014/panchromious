// jQuery Initialization
jQuery(document).ready(function($){
"use strict"; 
        
        $(document).ready(function() {
            $('.fades, .bounces, .zooms, .flips').on('change', function() {
                var elem = $('#sandbox'),
                    effect = $(this).val();

                elem.removeClass().addClass('animating bm-remove').addClass(effect);

                elem.one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function() {
                    elem.removeClass(effect).removeClass('animating');
                });
            });

            $('.show-off').on('change', function() {
                var elem = $('#sandbox'),
                    effect = $(this).val(),
                    exit = $(this).attr('data-exit');

                if (exit) animateEnd('#sandbox', true, true, effect);
                else animate('#sandbox', effect);
            });
        });
        
});