/**
 * Created by oksana on 7/14/17.
 */

function agentTransitions (){
    ///////////////////////////////////////////////////////////////////////////
    //////////////////// Set up and initiate svg containers ///////////////////
    ///////////////////////////////////////////////////////////////////////////

    var margin = {
        top: 0,
        right: 0,
        bottom: 0,
        left: 0
    };
    var width = 400,
        height = 200;

    //SVG container
    var svg = d3.select('#simpleCircle')
        .append("svg")
        .attr("width", width + margin.left + margin.right)
        .attr("height", height + margin.top + margin.bottom)
        .append("g")
        .attr("transform", "translate(" + (margin.left + width/2) + "," + (margin.top + height/2) + ")");

    ///////////////////////////////////////////////////////////////////////////
    ///////////////////////////// Create gradient /////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    //SVG filter for the fuzzy effect
    //Code based on http://tympanus.net/codrops/2015/04/08/motion-blur-effect-svg/
    var defs = svg.append("defs"); // to be defined for later reuse

    //Create a filter per circle so we can adjust the fuzzyness per circle that is flying out
    defs.append("filter")
        .attr("id", "fuzzyFilter")
        .attr("width", "300%")	//increase the width of the filter region to remove blur "boundary"
        .attr("x", "-100%") //make sure the center of the "width" lies in the middle
        .attr("color-interpolation-filters","sRGB") //to fix safari: http://stackoverflow.com/questions/24295043/svg-gaussian-blur-in-safari-unexpectedly-lightens-image
        .append("feGaussianBlur")
        .attr("class", "blurValues")
        .attr("in","SourceGraphic")
        .attr("stdDeviation","0 0"); //start without a blur

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////// Place circles inside hexagon ///////////////////////
    ///////////////////////////////////////////////////////////////////////////

    var radius = 8;

    //Finally append the visible circles
    svg.selectAll(".fuzzyCircle")
        .data([-1,1])
        .enter().append("circle")
        .attr("class", function(d,i) { return "fuzzyCircle circleNumber-" + i; })
        .attr("cx", function(d,i) { return i * -width/2*0.9; })
        .attr("cy", function(d) { return d*radius*3; })
        .attr("r", radius)
        .style("fill", "#F92672")
        .style("filter", "url(#fuzzyFilter)");

    ///////////////////////////////////////////////////////////////////////////
    ////////////////////////// Create blur on and off /////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    //Start the movement of the bottom circle
    d3.select(".circleNumber-1")
        .each(slide);

    //Slide the circle from left to right
    function slide(d) {
        var circle = d3.select(this);
        var element = d;
        (function repeat() {

            //Not very elegant, but do the motion blur on the left movement
            //and later again on the right movement of the bottom circle
            createBlur();
            setTimeout(createBlur, 900);

            //Move the circle left and right
            circle = circle.transition().duration(900)
                .attr("cx", width/2*0.9 )
                .transition().duration(900)
                .attr("cx", -width/2*0.9 )
                .each("end", repeat);
        })();
    }//slide

    //Adjust the motion blur filter
    function createBlur() {
        //Interpolate the motion blur
        d3.select("#fuzzyFilter .blurValues") //select the feGaussianBlur
        //Step 1: transition the filter from 0 blur to a heavy blur - start of a circle movement
            .transition().duration(300)
            .delay(200)
            .attrTween("stdDeviation", function() { return d3.interpolateString("0 0", "4 0"); })
            //Step 2: transition the filter from heavy blur to a 0 blur - end of a circle movement
            //besides the interpolator now goign from 8 to 0 the rest is the same as above
            .transition().duration(250)
            .attrTween("stdDeviation", function() { return d3.interpolateString("4 0", "0 0"); });
    }//createBlur
}
agentTransitions();