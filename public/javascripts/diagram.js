var flotrDiagram = (function (){

	/* public methods are declared below */	

	var pub = {

			account_hash: "J8Q0WIvYlb",
			server: "Icarus", 
			period: "1d", 
			attribute_name:"Cpu",	
			maxTick: 0, 
			minTick: 0,   
			points: [],

			update: function(options){
				priv.setOptions(options);
				var container = document.getElementById("diagram");
				if (container != null){
					console.log("uploading.....");
					var rand = Math.floor(Math.random() * (1000000)) + 1;
					$.ajax({
						url: '/diagram.json?r=' + rand, 
						dataType : "json",
						data: {
							account_hash: pub.account_hash,
							server: pub.server, 
							period: pub.period, 
							attribute_name: pub.attribute_name
						},
						success: function (json, textStatus) {
							$('#diagram').html('');
							pub.minTick = json.minTick;
							pub.maxTick = json.maxTick;
							pub.points = json.points;
							priv.fixLastUpdateVal(json.lastUpdate);
							console.log('success: ');
							console.log(json);
							pub.redraw();
						}               
					});
					priv.startUpdating();
				}else{
					priv.stopUpdating();
				}
			},
			
			
			redraw: function(){
				var container = document.getElementById("diagram");
				if (container == null) return;
				var graph = Flotr.draw(container, priv.getFlotrData(), priv.getFlotrOptions());
			}

	}

	/* below this line - private methods of the module are declared */

	var priv = {
			intervalID: 0,
			firstUpload: true,

			getFlotrData: function(){
				return [{
					data : pub.points, 
					lines : { show : true, fillColor: ['#00A8F0', '#fff'], fill: true },
					points : { show : true } 
				}];
			},
			
			getFlotrOptions: function(){
				return {
					title : pub.attribute_name,
					preventDefault : false,
					xaxis: {
						ticks : priv.getTicks(),
						max: pub.maxTick,
						min: pub.minTick,

						tickFormatter: function(n) {
							var date = new Date(n*1);
							if ( (pub.period == "1d")||(pub.period == "8h")||(pub.period.toLowerCase() == "Now")){
								return ('0' + date.getHours()).substr(-2,2) 
								+ ':' 
								+ ('0' + date.getMinutes()).substr(-2,2);
							}else{
								return date.getFullYear() + '-' 
								+ ('0' + (date.getMonth()+1)).substr(-2,2) + '-' 
								+ ('0' + date.getDate()).substr(-2,2) ;
							}
						}
					},
					yaxis: {
						noTicks: 5,
						tickFormatter: function(n) {
							return n + "%";
						},
						min: 0,
						max: 100
					},
					grid: {
						minorVerticalLines: true
					}
				}
			},
			
			getTicks: function(){
				var maxTick = pub.maxTick;
				var minTick = pub.minTick;
				var buf = new Date(minTick);
				var diff = maxTick - minTick;			            

				//less than two hours
				if( diff < (2*60*60*1000) ){
					var stepMinutes = 15;
					var stepMs = stepMinutes * 60 * 1000;						
					buf.setMinutes(buf.getMinutes() - (buf.getMinutes()%stepMinutes) + stepMinutes);	

					//less than 9 hours
				} else if( diff < (9*60*60*1000) ){
					var stepHours = 2;
					var stepMs = stepHours * 60 * 60 * 1000;						
					buf.setHours(buf.getHours() - (buf.getHours()%stepHours) + stepHours);		
					buf.setMinutes(0);

					//less than 25 hours
				} else if( diff < (25*60*60*1000) ){
					var stepHours = 4;
					var stepMs = stepHours * 60 * 60 * 1000;						
					buf.setHours(buf.getHours() - (buf.getHours()%stepHours) + stepHours);		
					buf.setMinutes(0);

					//less than 8 days
				} else if( diff < (9*24*60*60*1000) ){
					var stepDays = 2;
					var stepMs = stepDays * 24 * 60 * 60 * 1000;						
					buf.setDate(buf.getDate() - (buf.getDate()%stepDays) + stepDays);

					//less than 32 days
				} else if( diff < (32*24*60*60*1000) ){
					var stepDays = 6;
					var stepMs = stepDays * 24 * 60 * 60 * 1000;						
					buf.setDate(buf.getDate() - (buf.getDate()%stepDays) + stepDays);
				}

				minTick = buf.getTime();
				var t1 = [];					
				for(var i = minTick; i<=maxTick; i+=stepMs){
					buf = new Date(i);						
					t1.push(buf.getTime());	
				}
				return t1;
			},
			
			fixLastUpdateVal: function(lastUpdate){
				$('#last-update').attr('timestamp', lastUpdate);
				fixTimeZone();
			},
			
			
			startUpdating: function(){
				if( priv.intervalID > 0) return;
				priv.intervalID = setInterval(function(){
					flotrDiagram.update();
				}, 60000);
			},
			
			stopUpdating: function(){
				if( priv.intervalID == 0) return;
				clearInterval(priv.intervalID);
				priv.intervalID = 0;
			},
			
			setOptions: function(options){
				for(prop in options) {
					if (!options.hasOwnProperty(prop)) continue;
					if (pub.hasOwnProperty(prop)){
						pub[prop] = options[prop];
					}
				}
			}			
			
	}

	return pub;
}()); //diagram module end


$(window).resize(function() {
	flotrDiagram.redraw();
});


$(function(){
	
})



