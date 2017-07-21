/**
 * Created by oksana on 7/20/17.
 */

//require('chartist-plugin-legend');
d3.csv("data/table/buftable_14_06_3ver_modif.csv", function(error, abund_t) {
    if (error) throw error;
    else console.log("===" + "Table was written." + "=== \n");

    var lCh = new LineChart(abund_t);
    var nTick=0;
    d3.interval(function () {
        lCh.agentTick(++nTick);

    }, 100);

   /* var time = abund_t.length;
    var infPers = [];
    var infResPers = [];
    var healthHosp = [];
    var infHosp = [];
    for (var j = 0, len = abund_t.length; j < len; j += 1) {
        infPers.push(parseInt(abund_t[j]["InfectedPerson"]));
        infResPers.push(parseInt(abund_t[j]["InfectedPersonsWithResistantPathogen"]));
        healthHosp.push(parseInt(abund_t[j]["HospitalisedPersonsWithOtherProblems"]));
        infHosp.push(parseInt(abund_t[j]["HospitalisedPersonsWithPathogen"]));
    };
/!*
    for (var j = 0, len = abund_t.length; j < len; j += 1){
        new Chartist.Line('.ct-chart', {//to4ra zna4it class - to look about css selectors
            labels: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
            series: [
                infPers.slice(0, j+1),
                infResPers.slice(0, j+1),
                healthHosp.slice(0, j+1),
                infHosp.slice(0, j+1)
            ]
        }, {
            fullWidth: true,
            chartPadding: {
                right: 40
            }
        });
    }*!/

    var nTick = 0;

    function getSeries(nTick) {
        return [
            {"name": "Infected persons", "data": infPers.slice(0, nTick+1)},
            {"name": "Infected persons wth res path", "data": infResPers.slice(0, nTick+1)},
            {"name": "Healthy hospitalized", "data": healthHosp.slice(0, nTick+1)},
            {"name": "Infected hospitalized", "data": infHosp.slice(0, nTick+1)}
        ];
    }


    var lineCh = new Chartist.Line('.ct-chart', {//to4ra zna4it class - to look about css selectors
        //labels: ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday'],
        series: getSeries(nTick)
        //     [
        //     { "name": "Infected persons", "data": infPers },
        //     { "name": "Infected persons wth res path", "data": infResPers},
        //     { "name": "Healthy hospitalized", "data": healthHosp},
        //     { "name": "Infected hospitalized", "data": infHosp}
        // ]
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

    d3.interval(function () {
        lineCh.update({series: getSeries(++nTick)});
    }, 500);
/!*    document.querySelectorAll('[data-chart]').forEach(function(elem) {
        var chart = charts[elem.getAttribute('data-chart')];
        new chart.type(elem, chart.data, chart.options);
    });*!/*/

});

