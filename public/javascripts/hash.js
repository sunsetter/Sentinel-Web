/**
 * Handling hash changing events
 */


// jquery document.ready handler
$(function(){
	hashModule.updateThingsFromHash();	
    $(window).on('hashchange', function(){
    	hashModule.updateThingsFromHash();
    })
})


/**
 * Search module - defines event handlers for the search page and exports them, but also contains
 * private methods and members these handlers may use
 */
var hashModule = (function (){

    /* public methods are declared below */

    var pub = {
    	
    	historyBack:"",
    	pageTitle:"",

        updateThingsFromHash: function() {
            if (!priv.hashChangeHandlerEnabled) return;
            priv.hashChangeHandlerEnabled = false;
			var hash = "";
			
			// to prevent FF issue with autoencoding window.location.hash
			if (window.location.hash) {
				hash  = window.location.href.toString().split('#')[1];
			}

			if (window.location.hash.length < 2) {
				return;
			}
			pub.historyBack='';
			
			console.log("hash: " + hash);
			
			priv.showIndicator();
			$.ajax({
			    url: hash,
			    dataType : "html",
			    success: function (data, textStatus) {
			    	$('#content').html(data);
			    	priv.hashChangeHandlerEnabled = true;
			    	priv.hideIndicator();
			    	priv.updateTitle();
			    	priv.updateHistoryBack();			    				    	
			    },
			    error: function (data, textStatus) {
			    	priv.hashChangeHandlerEnabled = true;
			    	priv.hideIndicator();
			    	if(data.status == 404){
			    		$('#content .well').html('Account not found.');
			    	}else{
			    		$('#content .well').html("Connection error.");
			    	}
			    }       
			});
        }
    	
    }

    /* below this line - private methods of the module are declared */

    var priv = {
        hashChangeHandlerEnabled: true,
        
        showIndicator: function(mode) {
            $('#content .well').addClass('indicator');
        },
        
        hideIndicator: function(mode) {
        	$('#content .well').removeClass('indicator');
        },
        
        updateTitle: function(){
        	var title = pub.pageTitle;
        	if (title.length > 0){
        		$('#title').html(title);
        	}
        },
        updateHistoryBack: function(){
        	var historyBack = pub.historyBack;
        	if (historyBack.length > 0){
        		$('#historyBack').attr("href", "#" + historyBack);
        		$('#historyBack').show();
        	}else{
        		$('#historyBack').hide();
        	}
        }
    }

    return pub;
}()); 



