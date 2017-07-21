/**
 * Created by oksana on 7/14/17.
 */

d3.csv("data/table/transition_test_table.csv", function(error, data) {
    if (error) throw error
    else console.log("==="+ "Table was written." +"=== \n");
    function agentTransitons(data) {
        var max_ticks = d3.max(data, function(d) { return +d.Ticks;} );
        var colNmaes = d3.keys(data[0]);
        colNmaes.shift();

        console.log("==="+"col "+colNmaes+"=== \n");
        var num = colNmaes.length;

        var canvas = document.getElementById("canvas");
        var width = 500;
        canvas.width = width +50;
        var height = 500;
        canvas.height = height + 50;
        var ctx = canvas.getContext("2d");

        /*var stateCoord = []; // empty array
        for (var i = 0; i < colNmaes.length; i++) {
            stateCoord.push({ name: colNmaes[i], value: [] });
            //stateCoord.push({ name: 'ghi', value: 'jkl' });
        };*/
        var stateName = ["townIncPerPersons","townHealthyPersons","healthyHospPeople","townAntTrPersons2","townAntTrPersons","hospAntTrPersons","townIncPerPersons2"];
        var stateCoord = [];
        stateCoord.push({ name: stateName[0], value: [0,0] });
        stateCoord.push({ name: stateName[1], value: [1/3*width,1/3*height] });
        stateCoord.push({ name: stateName[2], value: [0,height] });
        stateCoord.push({ name: stateName[3], value: [1/2*width,2/3*height] });
        stateCoord.push({ name: stateName[4], value: [width,height] });
        stateCoord.push({ name: stateName[5], value: [2/3*width,1/3*height] });
        stateCoord.push({ name: stateName[6], value: [width,0] });


        function findElement(arr, propName, propValue) {
            for (var i=0; i < arr.length; i++)
                if (arr[i][propName] == propValue)
                    return arr[i];

            // will return undefined if not found; you could return a default instead
        }

        // Using the array from the question
        //console.log("====stateCoord===="+ alert(findElement(stateCoord, "name", "townIncPerPersons")["value"]));

        var particles = d3.range(num).map(function(i) {
            return [Math.round(width*Math.random()), Math.round(height*Math.random()), "pers"+(i+1)];
        });

        timeTicks = 1;

        var myTimer = d3.timer(step);

        function step() {
            ctx.fillStyle = "rgba(255,255,255,0.3)";
            ctx.fillRect(0,0,width,height);
            for (var ind=0; ind < stateCoord.length; ind++){
                console.log("rgba("+(ind*35)+","+(255-ind*35)+",255,0.4)");
                buf = "rgba("+(ind*35)+","+(255-ind*35)+",255,0.4)"
                ctx.fillStyle = buf;
                ctx.fillRect(stateCoord[ind].value[0],stateCoord[ind].value[1],25,25);
            }
            ctx.fillStyle = "rgba(0,0,0,0.5)";
            particles.forEach(function(p) {
                // it will be useful for next feature, that agents should be in some reactangle in some state
                p[0] += Math.round(2*Math.random()-1);
                p[1] += Math.round(2*Math.random()-1);
                if (p[0] < 0) p[0] = width;
                if (p[0] > width) p[0] = 0;
                if (p[1] < 0) p[1] = height;
                if (p[1] > height) p[1] = 0;
                // alert выводит на экран окно с сообщением и приостанавливает выполнение скрипта, пока пользователь не нажмёт «ОК».
                //tmpVal = alert(findElement(data, "name", p[2])["value"]);//[timeTicks];


                //console.log("====stateCoord===="+ alert(findElement(data, "name", p[2])["value"]));
                //console.log("try for timeTicks="+timeTicks+" and "+ p[2] +"state is " +data[timeTicks][p[2]]);
                tmpVal= findElement(stateCoord, "name", data[timeTicks][p[2]])["value"];
                console.log(data[timeTicks]);
                console.log(findElement(stateCoord, "name", data[timeTicks]["pers1"])["value"]);
                //console.log("stateCoord is "+ tmpVal[0]+" and "+tmpVal[1]);

                //console.log("try "+alert(findElement(stateCoord, "name", data[timeTicks][p[2]])["value"])+ p[2]);

                drawPoint-+
                     (tmpVal);


            });
            timeTicks += 1;
            if (timeTicks >= max_ticks){
                myTimer.exit().classed('exiting', true);;
            }
        };

        function drawPoint(p) {
            ctx.fillRect(p[0],p[1],5,5);
        };
    }

    agentTransitons(data);

    }
);