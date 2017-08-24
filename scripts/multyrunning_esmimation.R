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
test.dt <- dt[order(dt$Ticks),c("Ticks","simulation","AllInfectedPer")]

test.dt.est <- merge(aggregate(data = test.dt, AllInfectedPer ~ Ticks, FUN = function(x) mean(x)),
                     aggregate(data = test.dt, AllInfectedPer ~ Ticks, FUN = function(x) sd(x)), by="Ticks")
colnames(test.dt.est) <- c("Ticks", "mn", "std.dev")
test.dt.extr <- merge(aggregate(data = test.dt, AllInfectedPer ~ Ticks, FUN = function(x) min(x)),
                      aggregate(data = test.dt, AllInfectedPer ~ Ticks, FUN = function(x) max(x)), by="Ticks")
colnames(test.dt.extr) <- c("Ticks", "min", "max")
#aggregate(data = test.dt, AllInfectedPer ~ Ticks, FUN = function(x) c(mn = mean(x), std.dev = sd(x) )) #mean)
head(test.dt.est)
dev.off()

with(test.dt.est, 
     plot(0,0,ylim=c(min(0, mn-std.dev),max(test.dt.extr$max, mn+std.dev)), xlim=c(0,length(Ticks)),
          pch=".", xlab="Ticks", ylab="Mean +/- SD",
          main="Estimation of model VERA"
     ))
with(test.dt.extr, polygon(c(Ticks, rev(Ticks)),c(min, rev(max)), col = rgb(0.7, 0.87, 0.41,alpha=0.2), lty=0 ))
with(test.dt.est,points(Ticks, mn, pch=19, col=rgb(0.7, 0.87, 0.41)))
# hack: we draw arrows but with very special "arrowheads"
with(test.dt.est, arrows(Ticks, mn - std.dev, Ticks, mn + std.dev, length=0.05, angle=90, code=3, col = rgb(0.7, 0.87, 0.41)))

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





