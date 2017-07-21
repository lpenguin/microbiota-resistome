/**
 * Created by oksana on 7/19/17.
 */
d3.csv("data/table/buftable_14_06_3ver_modif.csv", function(error, abund_t) {
    if (error) throw error
    else console.log("===" + "Table was written." + "=== \n");

    var infPers = [];
    var infResPers = [];
    var healthHosp = [];
    var infHosp = [];
    for (var j = 0, len = abund_t.length; j < len; j += 1){
        infPers.push([j+1, parseInt(abund_t[j]["InfectedPerson"])]);
        infResPers.push([j+1, parseInt(abund_t[j]["InfectedPersonsWithResistantPathogen"])]);
        healthHosp.push([j+1, parseInt(abund_t[j]["HospitalisedPersonsWithOtherProblems"])]);
        infHosp.push([j+1, parseInt(abund_t[j]["HospitalisedPersonsWithPathogen"])]);
    };

    var gArray = infPers.concat(infResPers,healthHosp,infHosp);
    var myChart = new JSChart('graph', 'line');
    /*arr1 = [[1, 80],[2, 40],[3, 60],[4, 65],[5, 50],[6, 50],[7, 60],[8, 80],[9, 150],[10, 100]];
    arr2 = [[1, 100],[2, 55],[3, 80],[4, 115],[5, 80],[6, 70],[7, 30],[8, 130],[9, 160],[10, 170]];
    arr3 = [[1, 150],[2, 25],[3, 100],[4, 80],[5, 20],[6, 65],[7, 0],[8, 155],[9, 190],[10, 200]];*/

    var max_y = Math.max.apply(Math,gArray.map(function(o){return o[1];}));
    var max_x = abund_t.length;
//d3.max(arr1, function(d) { return d[1]; })
//Math.max.apply(Math,arr1.map(function(o){return o[1];}))
    myChart.setDataArray(infPers, "#999999");
    myChart.setDataArray(infResPers, "#E69F00");
    myChart.setDataArray(infHosp, "#56B4E9");
    myChart.setDataArray(healthHosp, "#33a02c");
    var size_param = [550, 300];
    myChart.setSize(size_param[0], size_param[1]);
    //console.log(Math.ceil10(max_y/50, -1));
    myChart.setAxisValuesNumberY(Math.round(max_y/50)+1);
    myChart.setIntervalStartY(0);
    myChart.setIntervalEndY(max_y);
    /*myChart.setLabelX([2,'p1']);
    myChart.setLabelX([4,'p2']);
    myChart.setLabelX([6,'p3']);
    myChart.setLabelX([8,'p4']);
    myChart.setLabelX([10,'p5']);*/
    myChart.setAxisValuesNumberX(Math.round(max_x/10));
    myChart.setShowXValues(false);
    myChart.setTitleColor('#454545');
    myChart.setAxisValuesColor('#454545');
    myChart.setLineColor('#A4D314', 'green');
    myChart.setLineColor('#BBBBBB', 'gray');
    /*myChart.setTooltip([1,' ']);
    myChart.setTooltip([2,' ']);
    myChart.setTooltip([3,' ']);
    myChart.setTooltip([4,' ']);
    myChart.setTooltip([5,' ']);
    myChart.setTooltip([6,' ']);
    myChart.setTooltip([7,' ']);
    myChart.setTooltip([8,' ']);
    myChart.setTooltip([9,' ']);
    myChart.setTooltip([10,' ']);
    myChart.setFlagColor('#9D16FC');
    myChart.setFlagRadius(4);*/
    myChart.setAxisPaddingRight(100);
    myChart.setLegendShow(true);
    myChart.setLegendPosition(size_param[0]-60, size_param[1]-220);
    myChart.setLegendForLine("#999999", 'Click me');
    myChart.setLegendForLine("#E69F00", 'Click me');
    myChart.setLegendForLine("#56B4E9", 'Click me');
    myChart.setLegendForLine("#33a02c", 'Click me');
    myChart.draw();
});
