{"explodedSupergraphs":[{"data":[{"data":{"stmtId":7,"shortLabel":"nop","label":"nop","id":"stmt7","stmtIndex":0},"classes":"stmt label    method1 Forward","position":{"x":10,"y":598}},{"data":{"directed":"true","source":"stmt7","target":"stmt8"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":8,"shortLabel":"this := @this: targets.file.FileMustBeClosedTest","label":"this := @this: targets.file.FileMustBeClosedTest","id":"stmt8","stmtIndex":1},"classes":"stmt label    method1 Forward","position":{"x":10,"y":628}},{"data":{"directed":"true","source":"stmt8","target":"stmt9"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":9,"shortLabel":"$r0 = new targets.file.ObjectWithField","label":"$r0 = new targets.file.ObjectWithField","id":"stmt9","stmtIndex":2},"classes":"stmt label    method1 Forward","position":{"x":10,"y":658}},{"data":{"directed":"true","source":"stmt9","target":"stmt10"},"classes":"cfgEdge label method1 Forward"},{"data":{"callSite":true,"stmtId":10,"callees":[{"name":"&lt;targets.file.ObjectWithField: void &lt;init&gt;()&gt;","id":11,"direction":"Forward"}],"shortLabel":"$r0.<init>()","label":"specialinvoke $r0.<targets.file.ObjectWithField: void <init>()>()","id":"stmt10","stmtIndex":3},"classes":"stmt label   callSite  method1 Forward","position":{"x":10,"y":688}},{"data":{"directed":"true","source":"stmt10","target":"stmt12"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":12,"shortLabel":"container = $r0","label":"container = $r0","id":"stmt12","stmtIndex":4},"classes":"stmt label    method1 Forward","position":{"x":10,"y":718}},{"data":{"directed":"true","source":"stmt12","target":"stmt13"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":13,"shortLabel":"$r1 = new targets.file.File","label":"$r1 = new targets.file.File","id":"stmt13","stmtIndex":5},"classes":"stmt label    method1 Forward","position":{"x":10,"y":748}},{"data":{"directed":"true","source":"stmt13","target":"stmt14"},"classes":"cfgEdge label method1 Forward"},{"data":{"callSite":true,"stmtId":14,"callees":[{"name":"&lt;targets.file.File: void &lt;init&gt;()&gt;","id":2,"direction":"Forward"}],"shortLabel":"$r1.<init>()","label":"specialinvoke $r1.<targets.file.File: void <init>()>()","id":"stmt14","stmtIndex":6},"classes":"stmt label   callSite  method1 Forward","position":{"x":10,"y":778}},{"data":{"directed":"true","source":"stmt14","target":"stmt15"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":15,"shortLabel":"container.field = $r1","label":"container.<targets.file.ObjectWithField: targets.file.File field> = $r1","id":"stmt15","stmtIndex":7},"classes":"stmt label    method1 Forward","position":{"x":10,"y":808}},{"data":{"directed":"true","source":"stmt15","target":"stmt16"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":16,"shortLabel":"$r2 = new targets.file.ObjectWithField","label":"$r2 = new targets.file.ObjectWithField","id":"stmt16","stmtIndex":8},"classes":"stmt label    method1 Forward","position":{"x":10,"y":838}},{"data":{"directed":"true","source":"stmt16","target":"stmt17"},"classes":"cfgEdge label method1 Forward"},{"data":{"callSite":true,"stmtId":17,"callees":[{"name":"&lt;targets.file.ObjectWithField: void &lt;init&gt;()&gt;","id":11,"direction":"Forward"}],"shortLabel":"$r2.<init>()","label":"specialinvoke $r2.<targets.file.ObjectWithField: void <init>()>()","id":"stmt17","stmtIndex":9},"classes":"stmt label   callSite  method1 Forward","position":{"x":10,"y":868}},{"data":{"directed":"true","source":"stmt17","target":"stmt18"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":18,"shortLabel":"otherContainer = $r2","label":"otherContainer = $r2","id":"stmt18","stmtIndex":10},"classes":"stmt label    method1 Forward","position":{"x":10,"y":898}},{"data":{"directed":"true","source":"stmt18","target":"stmt19"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":19,"shortLabel":"a = container.field","label":"a = container.<targets.file.ObjectWithField: targets.file.File field>","id":"stmt19","stmtIndex":11},"classes":"stmt label    method1 Forward","position":{"x":10,"y":928}},{"data":{"directed":"true","source":"stmt19","target":"stmt20"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":20,"shortLabel":"otherContainer.field = a","label":"otherContainer.<targets.file.ObjectWithField: targets.file.File field> = a","id":"stmt20","stmtIndex":12},"classes":"stmt label    method1 Forward","position":{"x":10,"y":958}},{"data":{"directed":"true","source":"stmt20","target":"stmt21"},"classes":"cfgEdge label method1 Forward"},{"data":{"callSite":true,"stmtId":21,"callees":[{"name":"&lt;targets.file.FileMustBeClosedTest: void flowsToField(targets.file.ObjectWithField)&gt;","id":5,"direction":"Forward"}],"shortLabel":"this.flowsToField(container)","label":"specialinvoke this.<targets.file.FileMustBeClosedTest: void flowsToField(targets.file.ObjectWithField)>(container)","id":"stmt21","stmtIndex":13},"classes":"stmt label   callSite  method1 Forward","position":{"x":10,"y":988}},{"data":{"directed":"true","source":"stmt21","target":"stmt22"},"classes":"cfgEdge label method1 Forward"},{"data":{"callSite":true,"stmtId":22,"callees":[{"name":"&lt;test.IDEALTestingFramework: void mustBeInErrorState(java.lang.Object)&gt;","id":3,"direction":"Forward"}],"shortLabel":"this.mustBeInErrorState(a)","label":"virtualinvoke this.<targets.file.FileMustBeClosedTest: void mustBeInErrorState(java.lang.Object)>(a)","id":"stmt22","stmtIndex":14},"classes":"stmt label   callSite  method1 Forward","position":{"x":10,"y":1018}},{"data":{"directed":"true","source":"stmt22","target":"stmt23"},"classes":"cfgEdge label method1 Forward"},{"data":{"stmtId":23,"returnSite":true,"shortLabel":"return","label":"return","callers":[{"name":"&lt;dummyClass: void main(java.lang.String[])&gt;","id":4,"direction":"Forward"}],"id":"stmt23","stmtIndex":15},"classes":"stmt label  returnSite   method1 Forward","position":{"x":10,"y":1048}},{"data":{"factId":24,"label":"$r1"},"classes":"fact label method1 Forward","position":{"x":414,"y":568}},{"data":{"factId":25,"label":"container[<targets.file.ObjectWithField: targets.file.File field>]"},"classes":"fact label method1 Forward","position":{"x":444,"y":568}},{"data":{"factId":26,"label":"a"},"classes":"fact label method1 Forward","position":{"x":474,"y":568}},{"data":{"factId":27,"label":"otherContainer[<targets.file.ObjectWithField: targets.file.File field>]"},"classes":"fact label method1 Forward","position":{"x":504,"y":568}},{"data":{"factId":28,"label":"$r0[<targets.file.ObjectWithField: targets.file.File field>]"},"classes":"fact label method1 Forward","position":{"x":534,"y":568}},{"data":{"factId":29,"label":"$r2[<targets.file.ObjectWithField: targets.file.File field>]"},"classes":"fact label method1 Forward","position":{"x":564,"y":568}},{"data":{"stmtId":14,"factId":24,"ideValue":"BOTTOM","id":"n30"},"classes":"esgNode method1 Forward","position":{"x":414,"y":748},"group":"nodes"},{"data":{"stmtId":32,"factId":33,"id":"n31"},"classes":"esgNode method1 Forward","position":{"x":424,"y":748},"group":"nodes"},{"data":{"stmtId":35,"factId":33,"id":"n34"},"classes":"esgNode method1 Forward","position":{"x":424,"y":778},"group":"nodes"},{"data":{"stmtId":15,"factId":24,"ideValue":"[INIT]","id":"n36"},"classes":"esgNode method1 Forward","position":{"x":414,"y":778},"group":"nodes"},{"data":{"stmtId":16,"factId":24,"ideValue":"[INIT]","id":"n37"},"classes":"esgNode method1 Forward","position":{"x":414,"y":808},"group":"nodes"},{"data":{"stmtId":16,"factId":25,"ideValue":"[INIT]","id":"n38"},"classes":"esgNode method1 Forward","position":{"x":444,"y":808},"group":"nodes"},{"data":{"stmtId":17,"factId":24,"ideValue":"[INIT]","id":"n39"},"classes":"esgNode method1 Forward","position":{"x":414,"y":838},"group":"nodes"},{"data":{"stmtId":17,"factId":25,"ideValue":"[INIT]","id":"n40"},"classes":"esgNode method1 Forward","position":{"x":444,"y":838},"group":"nodes"},{"data":{"stmtId":18,"factId":24,"ideValue":"[INIT]","id":"n41"},"classes":"esgNode method1 Forward","position":{"x":414,"y":868},"group":"nodes"},{"data":{"stmtId":18,"factId":25,"ideValue":"[INIT]","id":"n42"},"classes":"esgNode method1 Forward","position":{"x":444,"y":868},"group":"nodes"},{"data":{"stmtId":19,"factId":24,"ideValue":"[INIT]","id":"n43"},"classes":"esgNode method1 Forward","position":{"x":414,"y":898},"group":"nodes"},{"data":{"stmtId":19,"factId":25,"ideValue":"[INIT]","id":"n44"},"classes":"esgNode method1 Forward","position":{"x":444,"y":898},"group":"nodes"},{"data":{"stmtId":20,"factId":24,"ideValue":"[INIT]","id":"n45"},"classes":"esgNode method1 Forward","position":{"x":414,"y":928},"group":"nodes"},{"data":{"stmtId":20,"factId":26,"ideValue":"[INIT]","id":"n46"},"classes":"esgNode method1 Forward","position":{"x":474,"y":928},"group":"nodes"},{"data":{"stmtId":20,"factId":25,"ideValue":"[INIT]","id":"n47"},"classes":"esgNode method1 Forward","position":{"x":444,"y":928},"group":"nodes"},{"data":{"stmtId":21,"factId":24,"ideValue":"[INIT]","id":"n48"},"classes":"esgNode method1 Forward","position":{"x":414,"y":958},"group":"nodes"},{"data":{"stmtId":21,"factId":26,"ideValue":"[INIT]","id":"n49"},"classes":"esgNode method1 Forward","position":{"x":474,"y":958},"group":"nodes"},{"data":{"stmtId":21,"factId":27,"ideValue":"[INIT]","id":"n50"},"classes":"esgNode method1 Forward","position":{"x":504,"y":958},"group":"nodes"},{"data":{"stmtId":21,"factId":25,"ideValue":"[INIT]","id":"n51"},"classes":"esgNode method1 Forward","position":{"x":444,"y":958},"group":"nodes"},{"data":{"stmtId":22,"factId":24,"ideValue":"[INIT]","id":"n52"},"classes":"esgNode method1 Forward","position":{"x":414,"y":988},"group":"nodes"},{"data":{"stmtId":22,"factId":26,"ideValue":"[INIT]","id":"n53"},"classes":"esgNode method1 Forward","position":{"x":474,"y":988},"group":"nodes"},{"data":{"stmtId":22,"factId":27,"ideValue":"[INIT]","id":"n54"},"classes":"esgNode method1 Forward","position":{"x":504,"y":988},"group":"nodes"},{"data":{"stmtId":56,"factId":57,"id":"n55"},"classes":"esgNode method1 Forward","position":{"x":454,"y":958},"group":"nodes"},{"data":{"stmtId":23,"factId":24,"ideValue":"[INIT]","id":"n58"},"classes":"esgNode method1 Forward","position":{"x":414,"y":1018},"group":"nodes"},{"data":{"stmtId":60,"factId":61,"id":"n59"},"classes":"esgNode method1 Forward","position":{"x":484,"y":988},"group":"nodes"},{"data":{"stmtId":23,"factId":27,"ideValue":"[INIT]","id":"n62"},"classes":"esgNode method1 Forward","position":{"x":504,"y":1018},"group":"nodes"},{"data":{"stmtId":64,"factId":61,"id":"n63"},"classes":"esgNode method1 Forward","position":{"x":484,"y":1018},"group":"nodes"},{"data":{"stmtId":23,"factId":26,"ideValue":"[INIT]","id":"n65"},"classes":"esgNode method1 Forward","position":{"x":474,"y":1018},"group":"nodes"},{"data":{"stmtId":67,"factId":57,"id":"n66"},"classes":"esgNode method1 Forward","position":{"x":454,"y":988},"group":"nodes"},{"data":{"stmtId":22,"factId":25,"ideValue":"[OPENED]","id":"n68"},"classes":"esgNode method1 Forward","position":{"x":444,"y":988},"group":"nodes"},{"data":{"stmtId":23,"factId":25,"ideValue":"[OPENED]","id":"n69"},"classes":"esgNode method1 Forward","position":{"x":444,"y":1018},"group":"nodes"},{"data":{"stmtId":16,"factId":28,"ideValue":"[INIT]","id":"n70"},"classes":"esgNode method1 Forward","position":{"x":534,"y":808},"group":"nodes"},{"data":{"stmtId":17,"factId":28,"ideValue":"[INIT]","id":"n71"},"classes":"esgNode method1 Forward","position":{"x":534,"y":838},"group":"nodes"},{"data":{"stmtId":18,"factId":28,"ideValue":"[INIT]","id":"n72"},"classes":"esgNode method1 Forward","position":{"x":534,"y":868},"group":"nodes"},{"data":{"stmtId":19,"factId":28,"ideValue":"[INIT]","id":"n73"},"classes":"esgNode method1 Forward","position":{"x":534,"y":898},"group":"nodes"},{"data":{"stmtId":20,"factId":28,"ideValue":"[INIT]","id":"n74"},"classes":"esgNode method1 Forward","position":{"x":534,"y":928},"group":"nodes"},{"data":{"stmtId":21,"factId":28,"ideValue":"[INIT]","id":"n75"},"classes":"esgNode method1 Forward","position":{"x":534,"y":958},"group":"nodes"},{"data":{"stmtId":22,"factId":28,"ideValue":"[INIT]","id":"n76"},"classes":"esgNode method1 Forward","position":{"x":534,"y":988},"group":"nodes"},{"data":{"stmtId":23,"factId":28,"ideValue":"[INIT]","id":"n77"},"classes":"esgNode method1 Forward","position":{"x":534,"y":1018},"group":"nodes"},{"data":{"stmtId":21,"factId":29,"ideValue":"[INIT]","id":"n78"},"classes":"esgNode method1 Forward","position":{"x":564,"y":958},"group":"nodes"},{"data":{"stmtId":22,"factId":29,"ideValue":"[INIT]","id":"n79"},"classes":"esgNode method1 Forward","position":{"x":564,"y":988},"group":"nodes"},{"data":{"stmtId":23,"factId":29,"ideValue":"[INIT]","id":"n80"},"classes":"esgNode method1 Forward","position":{"x":564,"y":1018},"group":"nodes"},{"data":{"directed":"true","id":"e81","source":"n30","target":"n31","direction":"Forward"},"classes":"esgEdge method1 callFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e82","source":"n34","target":"n36","direction":"Forward"},"classes":"esgEdge method1 returnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e83","source":"n36","target":"n37","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e84","source":"n36","target":"n38","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e85","source":"n37","target":"n39","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e86","source":"n38","target":"n40","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e87","source":"n39","target":"n41","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e88","source":"n40","target":"n42","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e89","source":"n41","target":"n43","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e90","source":"n42","target":"n44","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e91","source":"n43","target":"n45","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e92","source":"n44","target":"n46","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e93","source":"n44","target":"n47","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e94","source":"n45","target":"n48","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e95","source":"n46","target":"n49","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e96","source":"n46","target":"n50","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e97","source":"n47","target":"n51","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e98","source":"n48","target":"n52","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e99","source":"n49","target":"n53","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e100","source":"n50","target":"n54","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e101","source":"n51","target":"n55","direction":"Forward"},"classes":"esgEdge method1 callFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e102","source":"n52","target":"n58","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e103","source":"n53","target":"n59","direction":"Forward"},"classes":"esgEdge method1 callFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e104","source":"n54","target":"n62","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e105","source":"n63","target":"n65","direction":"Forward"},"classes":"esgEdge method1 returnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e106","source":"n66","target":"n68","direction":"Forward"},"classes":"esgEdge method1 returnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e107","source":"n68","target":"n69","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e108","source":"n70","target":"n71","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e109","source":"n71","target":"n72","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e110","source":"n72","target":"n73","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e111","source":"n73","target":"n74","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e112","source":"n74","target":"n75","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e113","source":"n75","target":"n76","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e114","source":"n76","target":"n77","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e115","source":"n78","target":"n79","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e116","source":"n79","target":"n80","direction":"Forward"},"classes":"esgEdge method1 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e117","source":"n36","target":"n70","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e118","source":"n46","target":"n78","direction":"Forward"},"classes":"esgEdge method1 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e119","source":"n55","target":"n66","direction":"Forward"},"classes":"esgEdge method1 summaryFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e120","source":"n59","target":"n63","direction":"Forward"},"classes":"esgEdge method1 summaryFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e121","source":"n31","target":"n34","direction":"Forward"},"classes":"esgEdge method1 summaryFlow Forward","group":"edges"}],"methodName":"&lt;targets.file.FileMustBeClosedTest: void fieldStoreAndLoad2()&gt;","methodId":1,"direction":"Forward"},{"data":[{"data":{"stmtId":32,"shortLabel":"nop","label":"nop","id":"stmt32","stmtIndex":0},"classes":"stmt label    method2 Forward","position":{"x":10,"y":62}},{"data":{"directed":"true","source":"stmt32","target":"stmt122"},"classes":"cfgEdge label method2 Forward"},{"data":{"stmtId":122,"shortLabel":"this := @this: targets.file.File","label":"this := @this: targets.file.File","id":"stmt122","stmtIndex":1},"classes":"stmt label    method2 Forward","position":{"x":10,"y":92}},{"data":{"directed":"true","source":"stmt122","target":"stmt123"},"classes":"cfgEdge label method2 Forward"},{"data":{"callSite":true,"stmtId":123,"callees":[],"shortLabel":"this.<init>()","label":"specialinvoke this.<java.lang.Object: void <init>()>()","id":"stmt123","stmtIndex":2},"classes":"stmt label   callSite  method2 Forward","position":{"x":10,"y":122}},{"data":{"directed":"true","source":"stmt123","target":"stmt35"},"classes":"cfgEdge label method2 Forward"},{"data":{"stmtId":35,"returnSite":true,"shortLabel":"return","label":"return","callers":[{"name":"&lt;targets.file.FileMustBeClosedTest: void fieldStoreAndLoad2()&gt;","id":1,"direction":"Forward"}],"id":"stmt35","stmtIndex":3},"classes":"stmt label  returnSite   method2 Forward","position":{"x":10,"y":152}},{"data":{"factId":33,"label":"this"},"classes":"fact label method2 Forward","position":{"x":286,"y":32}},{"data":{"stmtId":32,"factId":33,"id":"n124"},"classes":"esgNode method2 Forward","position":{"x":286,"y":32},"group":"nodes"},{"data":{"stmtId":122,"factId":33,"ideValue":"TOP","id":"n125"},"classes":"esgNode method2 Forward","position":{"x":286,"y":62},"group":"nodes"},{"data":{"stmtId":123,"factId":33,"id":"n126"},"classes":"esgNode method2 Forward","position":{"x":286,"y":92},"group":"nodes"},{"data":{"stmtId":35,"factId":33,"ideValue":"TOP","id":"n127"},"classes":"esgNode method2 Forward","position":{"x":286,"y":122},"group":"nodes"},{"data":{"directed":"true","id":"e128","source":"n124","target":"n125","direction":"Forward"},"classes":"esgEdge method2 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e129","source":"n125","target":"n126","direction":"Forward"},"classes":"esgEdge method2 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e130","source":"n126","target":"n127","direction":"Forward"},"classes":"esgEdge method2 call2ReturnFlow Forward","group":"edges"}],"methodName":"&lt;targets.file.File: void &lt;init&gt;()&gt;","methodId":2,"direction":"Forward"},{"data":[{"data":{"stmtId":60,"shortLabel":"nop","label":"nop","id":"stmt60","stmtIndex":0},"classes":"stmt label    method3 Forward","position":{"x":10,"y":94}},{"data":{"directed":"true","source":"stmt60","target":"stmt131"},"classes":"cfgEdge label method3 Forward"},{"data":{"stmtId":131,"shortLabel":"this := @this: test.IDEALTestingFramework","label":"this := @this: test.IDEALTestingFramework","id":"stmt131","stmtIndex":1},"classes":"stmt label    method3 Forward","position":{"x":10,"y":124}},{"data":{"directed":"true","source":"stmt131","target":"stmt132"},"classes":"cfgEdge label method3 Forward"},{"data":{"stmtId":132,"shortLabel":"variable := @parameter0: java.lang.Object","label":"variable := @parameter0: java.lang.Object","id":"stmt132","stmtIndex":2},"classes":"stmt label    method3 Forward","position":{"x":10,"y":154}},{"data":{"directed":"true","source":"stmt132","target":"stmt64"},"classes":"cfgEdge label method3 Forward"},{"data":{"stmtId":64,"returnSite":true,"shortLabel":"return","label":"return","callers":[{"name":"&lt;targets.file.FileMustBeClosedTest: void fieldStoreAndLoad2()&gt;","id":1,"direction":"Forward"}],"id":"stmt64","stmtIndex":3},"classes":"stmt label  returnSite   method3 Forward","position":{"x":10,"y":184}},{"data":{"factId":61,"label":"variable"},"classes":"fact label method3 Forward","position":{"x":358,"y":64}},{"data":{"stmtId":60,"factId":61,"ideValue":"[INIT]","id":"n133"},"classes":"esgNode method3 Forward","position":{"x":358,"y":64},"group":"nodes"},{"data":{"stmtId":131,"factId":61,"ideValue":"[INIT]","id":"n134"},"classes":"esgNode method3 Forward","position":{"x":358,"y":94},"group":"nodes"},{"data":{"stmtId":132,"factId":61,"ideValue":"[INIT]","id":"n135"},"classes":"esgNode method3 Forward","position":{"x":358,"y":124},"group":"nodes"},{"data":{"stmtId":64,"factId":61,"ideValue":"[INIT]","id":"n136"},"classes":"esgNode method3 Forward","position":{"x":358,"y":154},"group":"nodes"},{"data":{"directed":"true","id":"e137","source":"n133","target":"n134","direction":"Forward"},"classes":"esgEdge method3 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e138","source":"n134","target":"n135","direction":"Forward"},"classes":"esgEdge method3 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e139","source":"n135","target":"n136","direction":"Forward"},"classes":"esgEdge method3 normalFlow Forward","group":"edges"}],"methodName":"&lt;test.IDEALTestingFramework: void mustBeInErrorState(java.lang.Object)&gt;","methodId":3,"direction":"Forward"},{"data":[{"data":{"stmtId":140,"shortLabel":"nop","label":"nop","id":"stmt140","stmtIndex":0},"classes":"stmt label    method4 Forward","position":{"x":10,"y":30}},{"data":{"directed":"true","source":"stmt140","target":"stmt141"},"classes":"cfgEdge label method4 Forward"},{"data":{"stmtId":141,"shortLabel":"dummyObj = new targets.file.FileMustBeClosedTest","label":"dummyObj = new targets.file.FileMustBeClosedTest","id":"stmt141","stmtIndex":1},"classes":"stmt label    method4 Forward","position":{"x":10,"y":60}},{"data":{"directed":"true","source":"stmt141","target":"stmt142"},"classes":"cfgEdge label method4 Forward"},{"data":{"callSite":true,"stmtId":142,"callees":[{"name":"&lt;targets.file.FileMustBeClosedTest: void fieldStoreAndLoad2()&gt;","id":1,"direction":"Forward"}],"shortLabel":"dummyObj.fieldStoreAndLoad2()","label":"virtualinvoke dummyObj.<targets.file.FileMustBeClosedTest: void fieldStoreAndLoad2()>()","id":"stmt142","stmtIndex":2},"classes":"stmt label   callSite  method4 Forward","position":{"x":10,"y":90}}],"methodName":"&lt;dummyClass: void main(java.lang.String[])&gt;","methodId":4,"direction":"Forward"},{"data":[{"data":{"stmtId":56,"shortLabel":"nop","label":"nop","id":"stmt56","stmtIndex":0},"classes":"stmt label    method5 Forward","position":{"x":10,"y":558}},{"data":{"directed":"true","source":"stmt56","target":"stmt143"},"classes":"cfgEdge label method5 Forward"},{"data":{"stmtId":143,"shortLabel":"this := @this: targets.file.FileMustBeClosedTest","label":"this := @this: targets.file.FileMustBeClosedTest","id":"stmt143","stmtIndex":1},"classes":"stmt label    method5 Forward","position":{"x":10,"y":588}},{"data":{"directed":"true","source":"stmt143","target":"stmt144"},"classes":"cfgEdge label method5 Forward"},{"data":{"stmtId":144,"shortLabel":"container := @parameter0: targets.file.ObjectWithField","label":"container := @parameter0: targets.file.ObjectWithField","id":"stmt144","stmtIndex":2},"classes":"stmt label    method5 Forward","position":{"x":10,"y":618}},{"data":{"directed":"true","source":"stmt144","target":"stmt145"},"classes":"cfgEdge label method5 Forward"},{"data":{"stmtId":145,"shortLabel":"field = container.field","label":"field = container.<targets.file.ObjectWithField: targets.file.File field>","id":"stmt145","stmtIndex":3},"classes":"stmt label    method5 Forward","position":{"x":10,"y":648}},{"data":{"directed":"true","source":"stmt145","target":"stmt146"},"classes":"cfgEdge label method5 Forward"},{"data":{"callSite":true,"stmtId":146,"callees":[{"name":"&lt;targets.file.File: void open()&gt;","id":6,"direction":"Forward"}],"shortLabel":"field.open()","label":"virtualinvoke field.<targets.file.File: void open()>()","id":"stmt146","stmtIndex":4},"classes":"stmt label   callSite  method5 Forward","position":{"x":10,"y":678}},{"data":{"directed":"true","source":"stmt146","target":"stmt67"},"classes":"cfgEdge label method5 Forward"},{"data":{"stmtId":67,"returnSite":true,"shortLabel":"return","label":"return","callers":[{"name":"&lt;targets.file.FileMustBeClosedTest: void fieldStoreAndLoad2()&gt;","id":1,"direction":"Forward"}],"id":"stmt67","stmtIndex":5},"classes":"stmt label  returnSite   method5 Forward","position":{"x":10,"y":708}},{"data":{"factId":57,"label":"container[<targets.file.ObjectWithField: targets.file.File field>]"},"classes":"fact label method5 Forward","position":{"x":462,"y":528}},{"data":{"factId":147,"label":"field"},"classes":"fact label method5 Forward","position":{"x":492,"y":528}},{"data":{"stmtId":56,"factId":57,"ideValue":"[INIT]","id":"n148"},"classes":"esgNode method5 Forward","position":{"x":462,"y":528},"group":"nodes"},{"data":{"stmtId":143,"factId":57,"ideValue":"[INIT]","id":"n149"},"classes":"esgNode method5 Forward","position":{"x":462,"y":558},"group":"nodes"},{"data":{"stmtId":144,"factId":57,"ideValue":"[INIT]","id":"n150"},"classes":"esgNode method5 Forward","position":{"x":462,"y":588},"group":"nodes"},{"data":{"stmtId":145,"factId":57,"ideValue":"[INIT]","id":"n151"},"classes":"esgNode method5 Forward","position":{"x":462,"y":618},"group":"nodes"},{"data":{"stmtId":146,"factId":147,"ideValue":"[INIT]","id":"n152"},"classes":"esgNode method5 Forward","position":{"x":492,"y":648},"group":"nodes"},{"data":{"stmtId":146,"factId":57,"ideValue":"[INIT]","id":"n153"},"classes":"esgNode method5 Forward","position":{"x":462,"y":648},"group":"nodes"},{"data":{"stmtId":155,"factId":156,"id":"n154"},"classes":"esgNode method5 Forward","position":{"x":502,"y":648},"group":"nodes"},{"data":{"stmtId":67,"factId":57,"ideValue":"[OPENED]","id":"n157"},"classes":"esgNode method5 Forward","position":{"x":462,"y":678},"group":"nodes"},{"data":{"stmtId":159,"factId":156,"id":"n158"},"classes":"esgNode method5 Forward","position":{"x":502,"y":678},"group":"nodes"},{"data":{"stmtId":67,"factId":147,"ideValue":"[OPENED]","id":"n160"},"classes":"esgNode method5 Forward","position":{"x":492,"y":678},"group":"nodes"},{"data":{"stmtId":159,"factId":156,"id":"n161"},"classes":"esgNode method5 Forward","position":{"x":472,"y":678},"group":"nodes"},{"data":{"directed":"true","id":"e162","source":"n148","target":"n149","direction":"Forward"},"classes":"esgEdge method5 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e163","source":"n149","target":"n150","direction":"Forward"},"classes":"esgEdge method5 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e164","source":"n150","target":"n151","direction":"Forward"},"classes":"esgEdge method5 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e165","source":"n151","target":"n152","direction":"Forward"},"classes":"esgEdge method5 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e166","source":"n151","target":"n153","direction":"Forward"},"classes":"esgEdge method5 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e167","source":"n152","target":"n154","direction":"Forward"},"classes":"esgEdge method5 callFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e168","source":"n153","target":"n157","direction":"Forward"},"classes":"esgEdge method5 call2ReturnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e169","source":"n158","target":"n160","direction":"Forward"},"classes":"esgEdge method5 returnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e170","source":"n161","target":"n157","direction":"Forward"},"classes":"esgEdge method5 returnFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e171","source":"n154","target":"n158","direction":"Forward"},"classes":"esgEdge method5 summaryFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e172","source":"n154","target":"n161","direction":"Forward"},"classes":"esgEdge method5 summaryFlow Forward","group":"edges"}],"methodName":"&lt;targets.file.FileMustBeClosedTest: void flowsToField(targets.file.ObjectWithField)&gt;","methodId":5,"direction":"Forward"},{"data":[{"data":{"stmtId":155,"shortLabel":"nop","label":"nop","id":"stmt155","stmtIndex":0},"classes":"stmt label    method6 Forward","position":{"x":10,"y":62}},{"data":{"directed":"true","source":"stmt155","target":"stmt173"},"classes":"cfgEdge label method6 Forward"},{"data":{"stmtId":173,"shortLabel":"this := @this: targets.file.File","label":"this := @this: targets.file.File","id":"stmt173","stmtIndex":1},"classes":"stmt label    method6 Forward","position":{"x":10,"y":92}},{"data":{"directed":"true","source":"stmt173","target":"stmt159"},"classes":"cfgEdge label method6 Forward"},{"data":{"stmtId":159,"returnSite":true,"shortLabel":"return","label":"return","callers":[{"name":"&lt;targets.file.FileMustBeClosedTest: void flowsToField(targets.file.ObjectWithField)&gt;","id":5,"direction":"Forward"}],"id":"stmt159","stmtIndex":2},"classes":"stmt label  returnSite   method6 Forward","position":{"x":10,"y":122}},{"data":{"factId":156,"label":"this"},"classes":"fact label method6 Forward","position":{"x":286,"y":32}},{"data":{"stmtId":155,"factId":156,"ideValue":"[INIT]","id":"n174"},"classes":"esgNode method6 Forward","position":{"x":286,"y":32},"group":"nodes"},{"data":{"stmtId":173,"factId":156,"ideValue":"[INIT]","id":"n175"},"classes":"esgNode method6 Forward","position":{"x":286,"y":62},"group":"nodes"},{"data":{"stmtId":159,"factId":156,"ideValue":"[INIT]","id":"n176"},"classes":"esgNode method6 Forward","position":{"x":286,"y":92},"group":"nodes"},{"data":{"directed":"true","id":"e177","source":"n174","target":"n175","direction":"Forward"},"classes":"esgEdge method6 normalFlow Forward","group":"edges"},{"data":{"directed":"true","id":"e178","source":"n175","target":"n176","direction":"Forward"},"classes":"esgEdge method6 normalFlow Forward","group":"edges"}],"methodName":"&lt;targets.file.File: void open()&gt;","methodId":6,"direction":"Forward"}],"directions":[{"value":"Forward"}],"methodList":[{"name":"&lt;targets.file.FileMustBeClosedTest: void fieldStoreAndLoad2()&gt;","id":1},{"name":"&lt;targets.file.File: void &lt;init&gt;()&gt;","id":2},{"name":"&lt;test.IDEALTestingFramework: void mustBeInErrorState(java.lang.Object)&gt;","id":3},{"name":"&lt;dummyClass: void main(java.lang.String[])&gt;","id":4},{"name":"&lt;targets.file.FileMustBeClosedTest: void flowsToField(targets.file.ObjectWithField)&gt;","id":5},{"name":"&lt;targets.file.File: void open()&gt;","id":6}]}