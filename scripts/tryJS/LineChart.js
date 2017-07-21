/**
 * Created by oksana on 7/21/17.
 */
function LineChart(inpTable) {
    var self = this;
    var time = inpTable.length;
    var infPers = [];
    var infResPers = [];
    var healthHosp = [];
    var infHosp = [];
    for (var j = 0, len = inpTable.length; j < len; j += 1) {
        infPers.push(parseInt(inpTable[j]["InfectedPerson"]));
        infResPers.push(parseInt(inpTable[j]["InfectedPersonsWithResistantPathogen"]));
        healthHosp.push(parseInt(inpTable[j]["HospitalisedPersonsWithOtherProblems"]));
        infHosp.push(parseInt(inpTable[j]["HospitalisedPersonsWithPathogen"]));
    };
    function getSeries(nTick) {
        return [
            {"name": "Infected persons", "data": infPers.slice(0, nTick+1)},
            {"name": "Infected persons wth res path", "data": infResPers.slice(0, nTick+1)},
            {"name": "Healthy hospitalized", "data": healthHosp.slice(0, nTick+1)},
            {"name": "Infected hospitalized", "data": infHosp.slice(0, nTick+1)}
        ];
    };
    var lineCh = new Chartist.Line('.ct-chart', {//to4ra zna4it class - to look about css selectors
        //labels: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
        series: getSeries(0)
    }, {
        showPoint: false,
        fullWidth: true,
        chartPadding: {
            right: 40
        },
        plugins: [
            Chartist.plugins.legend({})
        ],
        axisX:{
            type: Chartist.StepAxis,
            ticks: d3.range(100)
        },
        axisY: {
            type: Chartist.AutoScaleAxis,
            low: 0,
            high: 170
            //ticks: [0, 5, 10]                // the ticks don't show up
        }
    });

    self.agentTick = function (nTick) {
        /*d3.interval(function () {
            lineCh.update({series: getSeries(++nTick)});
        }, 500);*/
        //or
        lineCh.update({series: getSeries(nTick)});

    };
};