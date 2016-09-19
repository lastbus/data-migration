/**
 * Created by MK33 on 2016/9/19.
 */

// filter
app.filter('partition', function() {
    return function(partition) {
        var act = "error";
        if (partition) {
            act = "select partition";
        } else {
            act = "select records";
        }
        return act;
    }

});

