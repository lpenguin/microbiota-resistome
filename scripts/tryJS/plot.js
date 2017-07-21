/**
 * Created by oksana on 7/12/17.
 */

d3.csv("data/table/buftable_14_06_3ver_modif.csv", function(error, data) {
    if (error) throw error
    else console.log("==="+ "Table was written." +"=== \n");

    // parameters for plot building
    var param = {
        parentSelector: "#chart1",
        width: 600,//parseInt(d3.select('#chart').style('width'), 10),
        height: 300,//parseInt(d3.select('#chart').style('height'), 10),
        title: "Model states for Shigella",
        xColumn: "Ticks",
        xAxisName: "Simulations ticks",
        yLeftAxisName: "Count of ill agents in each states",//"Proportion from the total ill agents",
        series: [ // #999999", "#E69F00", "#56B4E9", "#33a02c"
            {yColumn: "InfectedPerson", color: "#999999", yAxis: "left"},
            {yColumn: "InfectedPersonsWithResistantPathogen", color: "#E69F00", yAxis: "left"},//, yAxis: "right"}
            {yColumn: "HospitalisedPersonsWithPathogen", color: "#56B4E9", yAxis: "left"},
            {yColumn: "HospitalisedPersonsWithOtherProblems", color: "#33a02c", yAxis: "left"}
        ]
    };

    function d3sChart (param,data){  //,dataGroup) {

        // check availability the object, where is displayed chart
        var selectedObj = null;
        if (param.parentSelector === null || param.parentSelector === undefined) {
            parentSelector = "body";
        }
        ;
        selectedObj = d3.select(param.parentSelector);
        if (selectedObj.empty()) {
            throw "The '" + param.parentSelector + "' selector did not match any elements.  Please prefix with '#' to select by id or '.' to select by class";
        }
        ;
        //remove previous chart
        d3.select(param.parentSelector+"_d3_simple_chart").remove();

        //---- different margins
        var margin = {top: 30, right: 40, bottom: 50, left: 50},
            width = param.width - margin.left - margin.right,
            height = param.height - margin.top - margin.bottom;

        // set the scale for the transfer of real values
        var xScale = d3.scaleLinear().range([0, width]);
        var yScaleLeft = d3.scaleLinear().range([height, 0]);

        //---- setting for plot scale
        // В D3 есть шкалы (scales) и оси (axis)
        // definition of data range for conversion coord at scales
        var xMin=d3.min(data, function(d) { return d[param.xColumn]; }),
            xMax=d3.max(data, function(d) { return d[param.xColumn]; }),
            yLeftMax=0;

        for (var j = 0, len1 = param.series.length; j < len1; j += 1) {
            var max_val = d3.max(data, function(d) { return +d[param.series[j].yColumn];} );
            tmpVal = max_val;
            console.log("==="+ "max for "+j+" is "+ max_val +"=== \n");
            /*tmpVal = d3.max(data, function(d) {
                console.log("==="+ "buf "+ d[param.series[j].yColumn] +"=== \n");
                return d[param.series[j].yColumn];
            });*/
            console.log("==="+ "Max for line "+j+" is "+ tmpVal +"=== \n");
            if (tmpVal>yLeftMax) {yLeftMax = tmpVal};

        }; // wrong max
        /*data.each(col) {
            tmpVal = d3.max(col);
            console.log("==="+ "buf "+ col +"=== \n");
            if (tmpVal>yLeftMax) {yLeftMax = tmpVal};
            console.log("==="+ "Max for line "+j+" is "+ tmpVal +"=== \n");

        };
        console.log("==="+ "yLeftMax "+ yLeftMax +"=== \n");*/

        xScale.domain([xMin,xMax]);
        yScaleLeft.domain([0,yLeftMax]);

        //---- setting for axis
        var xAxis = d3.axisBottom(xScale).tickSize(10);
        var yAxisLeft = d3.axisLeft(yScaleLeft);

        //---- drawing of edging plot frame
        var svg = selectedObj.append("svg")
            .attr("width", param.width).attr("height", param.height)
            .attr("id", param.parentSelector.substr(1)+"_d3_simple_chart");

        // outer border
        svg.append("rect").attr("width", param.width).attr("height", param.height)
            .style("fill", "none").style("stroke", "#ccc");

        // create group in svg for generate graph
        var g = svg.append("g").attr("transform", "translate(" + margin.left + "," + margin.top + ")")
            .attr("class", "legend");

        // add title
        g.append("text").attr("x", margin.left) .attr("y", 0 - (margin.top / 2))
            .attr("text-anchor", "middle").style("font-size", "14px")
            .text(param.title);

        //---- measurement units of axis setting
        g.append("text").attr("x", width/2).attr("dx", 40) .attr("y", height+30 )
            .attr("text-anchor", "end")
            .text(param.xAxisName);
        g.append("text").attr("transform", "rotate(-90)")
            .attr("x", 5) .attr("y", -70).attr("dy", 40)
            .attr("text-anchor", "end")
            .text(param.yLeftAxisName);

        // add axis
        g.append("g").attr("class", "x axis").attr("transform", "translate(0," + height + ")")
            .call(xAxis);
        g.append("g").attr("class", "y axis")
            .call(yAxisLeft)

        console.log("==="+ "Before loop"+"=== \n");

        //---- drawing code kernel
        for (var j = 0, len1 = param.series.length; j < len1; j += 1) {
            var series = param.series[j];
            var line = d3.line()
                .x(function(data) { return xScale(data[param.xColumn]); })
                .y(function(data) { return yScaleLeft(data[param.series[j].yColumn]); });
            console.log("==="+ "Path for line "+ j +"=== \n");

            // draw line
            g.append("path")
                .datum(data)
                .attr("d", line)
                .style("fill", "none")
                .style("stroke", param.series[j].color)
            ;

            // draw line
            g.append("path").datum(data.values)
                .style({"fill": "none", "stroke": param.series[j].color})
                //.attr("d", line);*/
        };

        // add legend for seies
        var legend = svg.append("g").attr("class", "legend").attr("height", 40).attr("width", 200)
            .attr("transform",(param.title == "") ? "translate(20,20)" : "translate(180,20)");

        legend.selectAll('rect').data(param.series).enter()
            .append("rect").attr("y", 0 - (margin.top / 2)).attr("x", function(d, i){ return i *  90;})
            .attr("width", 10).attr("height", 10)
            .style("fill", function(d) {return d.color; });

        legend.selectAll('text').data(param.series).enter()
            .append("text").attr("y", 0 - (margin.top / 2)+10).attr("x", function(d, i){ return i *  90 + 11;})
            .text(function(d) { return d.title; });


        console.log("===testTest"+ "buf "+ [1,2,20,60,3] +"=== \n");

    };
    d3sChart(param,data);

})
