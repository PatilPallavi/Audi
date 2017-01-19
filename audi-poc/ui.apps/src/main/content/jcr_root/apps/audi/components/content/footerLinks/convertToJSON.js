use(function () {

var proposerMap = [];

var tempProposerMap = this.nodeName;
log.error("***"+tempProposerMap);
try{
if (typeof tempProposerMap === 'string' || tempProposerMap instanceof String){
var item = tempProposerMap;
item = JSON.parse(item);
    if(item.hasOwnProperty("multivalues")){
                for(var cnt in item.multivalues) {
                    item.multivalues[cnt] = JSON.parse(item.multivalues[cnt]);
                }
    }

proposerMap.push(item)
}
}catch(e){
for(var i in tempProposerMap) {
var item = tempProposerMap[i];
item = JSON.parse(item);
 if(item.hasOwnProperty("multivalues")){
                for(var cnt in item.multivalues) {
                    item.multivalues[cnt] = JSON.parse(item.multivalues[cnt]);
                }
    }
proposerMap.push(item)
}
}

return {
proposerObjs : proposerMap
};
});
