use(function () {

	var proposerMap = [];

	var tempProposerMap = properties.linkMap;


	try{
		if (typeof tempProposerMap === 'string' || tempProposerMap instanceof String){
			var item = tempProposerMap;
			 item = JSON.parse(item);

			proposerMap.push(item)
		}
	}catch(e){
		for(var i in tempProposerMap) {
			var item = tempProposerMap[i];
			 item = JSON.parse(item);

			proposerMap.push(item)
		}


		}
		return {
			proposerObjs : proposerMap

		};
	});