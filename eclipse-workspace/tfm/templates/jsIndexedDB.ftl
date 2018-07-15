	<script type="text/javascript">

		var indexedDB = window.indexedDB || window.mozIndexedDB || window.webkitIndexedDB || window.msIndexedDB;

        function startDB() {
                        
            var req = indexedDB.open("${appName}", 1);

			req.onupgradeneeded = function(evt){
				console.log("openDB.onupgradedneeded");
				var db = evt.target.result;
		
			db.onerror = function(){
				console.log(database.errorCode);	
			};
	
			var transaction = evt.target.transaction;
<#list objects as obj>${obj}</#list>

			};

			req.onsuccess=function(evt){
				datastore=evt.target.result;
				console.log("DB opened!");
			};
	
			req.onerror = function(evt){
				console.error("openDB:", evt.target.errorCode);
			};
		}
		
		function encryptPass(value){
		
			var h = 0, l = value.length, i = 0;
  			if ( l > 0 )
    			while (i < l)
      				h = (h << 5) - h + value.charCodeAt(i++) | 0;
			return h.toString();
		}
	</script>