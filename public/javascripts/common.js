function fixTimeZone(){
	$('.timestamp').each(function(index) {
		var timestamp =parseInt( $(this).attr('timestamp')  );
	    if( timestamp > 0 ){
	    	 var date = new Date( timestamp );
	    	 var dateStr = date.getFullYear() + '-' 
			   + ('0' + (date.getMonth()+1)).substr(-2,2) + '-' 
			   + ('0' + date.getDate()).substr(-2,2) + " "
			   + ('0' + date.getHours()).substr(-2,2) 
   			   + ':' 
   			   + ('0' + date.getMinutes()).substr(-2,2);
	    	$(this).html( dateStr );
	    }
	});
}