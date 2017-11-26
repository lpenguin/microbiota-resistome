rm(list=ls(all=TRUE)); gc()
#current.directory <- "/home/vera/agent_resistome/microbiota-resistome/out"
rootDir <- dirname(dirname(rstudioapi::getActiveDocumentContext()$path))
setwd(rootDir)
library("reshape")
library(zoo)
library(data.table)

dt <- data.frame()
for (cfile in list.files('out/simulations/Jun_23_time_17_07/', pattern = '.txt' )) {
  cur.dt <- read.table(sprintf("out/simulations/Jun_23_time_17_07/%s", cfile), header = T, fill = T,
                       comment.char = '', quote = '')
  cur.dt$simulation <- cfile
  dt <- rbind(dt, cur.dt)
}
dt$simulation <- gsub('(.*)\\.txt', '\\1', dt$simulation)
dt$Ticks <- dt$Ticks+1
dt$AllInfectedPer <- with(dt, InfectedPersonsInTown + InfectedPersonsInHospital)
head(dt)

#multRunEstimatio.pdf
par(mfrow=c(2,2))
for (cname in c("AllInfectedPer", "pGetInfectedTown","AvMicResistance","AvPathResistance")){
  test.dt <- dt[order(dt$Ticks),c("Ticks","simulation",cname)]
  
  test.dt.est <- merge(aggregate(data = test.dt, as.formula(paste(cname,"~", "Ticks")), FUN = function(x) mean(x)),
                       aggregate(data = test.dt, as.formula(paste(cname,"~", "Ticks")), FUN = function(x) sd(x)), by="Ticks")
  colnames(test.dt.est) <- c("Ticks", "mn", "std.dev")
  test.dt.extr <- merge(aggregate(data = test.dt, as.formula(paste(cname,"~", "Ticks")), FUN = function(x) min(x)),
                        aggregate(data = test.dt, as.formula(paste(cname,"~", "Ticks")), FUN = function(x) max(x)), by="Ticks")
  colnames(test.dt.extr) <- c("Ticks", "min", "max")
  #aggregate(data = test.dt, AllInfectedPer ~ Ticks, FUN = function(x) c(mn = mean(x), std.dev = sd(x) )) #mean)
  head(test.dt.est)
  print(max(test.dt.est$std.dev))
  
  ### ----plots--- ###
  inf.plot <- with(test.dt.est, 
                   plot(0,0,ylim=c(min(0, mn-std.dev),max(test.dt.extr$max, mn+std.dev)), xlim=c(0,length(Ticks)),
                        pch=".", xlab="Modeling time, days", ylab=paste(cname, "mean +/- SD", sep = ", "),
                        main=paste("Estimation of model VERA \n(",cname,")",sep = ""),bty="n"
                   ))
  with(test.dt.est,points(Ticks, mn, pch=19, col="#d53e4f"))
  with(test.dt.est, arrows(Ticks, mn - std.dev, Ticks, mn + std.dev, length=0.05, angle=90, code=3, col = "#d53e4f"))
  
}
dev.off()




##########
with(test.dt.est, 
     plot(0,0,ylim=c(min(0, mn-std.dev),max(test.dt.extr$max, mn+std.dev)), xlim=c(0,length(Ticks)),
          pch=".", xlab="Modeling time, days", ylab="All infected people, mean +/- SD",
          main="Estimation of model VERA (all infected perople)",bty="n"#, type = "l", lwd=5, col= "#1b9e77"
     ))
plot(estim.t$l, estim.t$estim, type = "l", main="Estimation of monitoring intensity",  #main= paste("Opt.intens =", as.numeric(estim.t[estim.t$estim==min(as.numeric(estim.t$estim)),]$l), ",  sigma^2 =", sigma.sq, sep=" "),
     ylab = "Functional value", xlab = "Monitoring intensity", bty="n", lwd=5, col= "#253494")
# with(test.dt.extr, polygon(c(Ticks, rev(Ticks)),c(min, rev(max)), col = rgb(0.7, 0.87, 0.41,alpha=0.2), lty=0 ))
with(test.dt.est,points(Ticks, mn, pch=19, col="#d53e4f"))
# hack: we draw arrows but with very special "arrowheads"
with(test.dt.est, arrows(Ticks, mn - std.dev, Ticks, mn + std.dev, length=0.05, angle=90, code=3, col = "#d53e4f"))

# # plot of standart deviation
plot(test.dt.est$std.dev, main="Standart deviation of model VERA")

# # plot 
# with(test.dt, 
#      plot(0,0,ylim=c(0,max(AllInfectedPer)), xlim=c(0,max(Ticks)+1),
#           pch=".", xlab="Ticks", ylab="Values",
#           main="Estimation of model VERA"
#      ))
# lapply(unique(test.dt$simulation), FUN = function(num){
#   buf <- test.dt[test.dt$simulation==num,]
#   lines(test.dt.est$Ticks, buf[order(buf$Ticks),]$AllInfectedPer, type="l", lty=1,lwd=2, col="#33a02c")
# })





