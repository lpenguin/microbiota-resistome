# rm(list=ls(all=TRUE)); gc()
# #current.directory <- "/home/vera/agent_resistome/microbiota-resistome/out"
# rootDir <- dirname(dirname(rstudioapi::getActiveDocumentContext()$path))
# setwd(rootDir)
#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
# i run it so 
#' Rscript --vanilla modelPlots.R ../out/simulations/buftable_15_06.txt 
#' ../out/plots/Aug16/ ../out/log/transLog_14_06_2ver.txt Shigella
library("reshape")
library(zoo)
library(data.table)
# install.packages("rmarkdown")
# require(rmarkdown)

# args <- c("/media/oksana/data/worspaceJAVA/microbiota-resistome/out/simulations/Jun_23_time_17_07/",
#           "/media/oksana/data/worspaceJAVA/microbiota-resistome/resources/config.properties",
#           "/media/oksana/data/worspaceJAVA/microbiota-resistome/out/plots/estimation", 
#           "/media/oksana/data/worspaceJAVA/microbiota-resistome/resources/cp_notes")#"Shigella")

###---- Output directory ----###
out.folder <- args[3]
cp <- read.table (args[2], header = F, fill = TRUE, sep = "=", comment.char = '', quote = '', stringsAsFactors = F)
cp.notes <- read.table (args[4], header = T, fill = TRUE, sep = "\t", comment.char = '', quote = '', stringsAsFactors = F)
cp <- merge(cp, cp.notes, by.x = "V1", by.y = "name", all.x = T)
colnames(cp) <- c("Name", "Value","Character")

dt <- data.frame()
for (cfile in list.files(args[1], pattern = '.txt' )) {
  cur.dt <- read.table(sprintf(paste(args[1],"/","%s", sep=""), cfile), header = T, fill = T,
                       comment.char = '', quote = '')
  cur.dt$simulation <- cfile
  dt <- rbind(dt, cur.dt)
}
dt$simulation <- gsub('(.*)\\.txt', '\\1', dt$simulation)
dt$Ticks <- dt$Ticks+1
dt$AllInfectedPer <- with(dt, InfectedPersonsInTown + InfectedPersonsInHospital)
#head(dt)
n.run <- as.numeric(length(unique(dt$simulation)))

time.t <- max(dt$Ticks)
delta.tick <- 1
estim.t <- NULL
sigma.sq <- 0.04
constA <- time.t
all.lambda <- sapply(seq(0.1, 5, by=.1) , function(lambda) { # or so seq(1/time.t, 5, by=1/time.t)
  matog <- sapply(unique(dt$simulation) , function(sim) { # or so seq(1/time.t, 5, by=1/time.t)
    # lambda <- 0.2
    # sim <- "rep100"

    # infect.p <- dt[dt$simulation==sim,"HealthyPersonsInHospital"] #infeected people
    infect.p <- dt[dt$simulation==sim,"AllInfectedPer"] #infeected people
    step.m <- cumsum(rexp(time.t*lambda, lambda)) # steps moments, markov moments
    step.m <- step.m[step.m<(time.t+1)&step.m>=1] 
    observ <- infect.p[trunc(step.m)] #I dont like that we dont consider 0-step 
    observ.full <- unique(na.locf(merge(data.frame(val=observ, time=trunc(step.m)),data.frame(time=1:time.t), all.y = T), fromLast = F))
    observ.full[is.na(observ.full)] <- 0
    observ.full$real.v <- infect.p 
    observ.full$diff.sqr <- (observ.full$real.v - observ.full$val)^2
    # head(observ.full)
    # observ.full$is.na <- as.numeric(is.na(observ.full$val))
    # observ.full2 <- na.locf(observ.full, fromLast = F)
    
    # plot(1:time.t, infect.p, col="red", type = "o", lwd=3)
    # lines(trunc(step.m), observ, col="green",lwd=1)

    return(sum(observ.full$diff.sqr)*delta.tick)
  })
  matog.est <- constA*lambda + sigma.sq*(sum(matog)/length(unique(dt$simulation))) # 1- constA, 100- kol trajectories
  # matog.est <- 1*lambda + (sum(matog)/length(unique(dt$simulation))) # 1- constA, 100- kol trajectories
  return(data.frame(l=lambda, estim=matog.est))
})
estim.t <- as.data.frame(t(all.lambda))
#head(estim.t)
pdf(file=paste(out.folder,"/tmp/plot.pdf", sep=""), height = 5, width = 5)
#png(file=paste(out.folder,"/tmp/plot.png", sep=""), height = 450, width = 450)
plot(estim.t$l, estim.t$estim, type = "l", main="Estimation of monitoring intensity",  #main= paste("Opt.intens =", as.numeric(estim.t[estim.t$estim==min(as.numeric(estim.t$estim)),]$l), ",  sigma^2 =", sigma.sq, sep=" "),
     ylab = "Functional value", xlab = "Monitoring intensity", bty="n", lwd=5, col= "#1b9e77")
     #sub = paste("Figure 1. Estimation of monitoring intensity,\n","where optimal intensity is equal to", as.numeric(estim.t[estim.t$estim==min(as.numeric(estim.t$estim)),]$l),".", sep=" "))#"#4daf4a")#"#e31a1c") #"#f0027f")
abline(v=as.numeric(estim.t[estim.t$estim==min(as.numeric(estim.t$estim)),]$l), col="#7570b3", lwd=2, lty=2)
dev.off()
# abline(h=median(as.numeric(estim.t$estim)), col="blue")

# min(as.numeric(estim.t$estim))
# print("##############################")
# print("#### Estimation of optimal ###")
# print("### observations intensity ###")
# print("##############################")
# print(paste("Optimal intensity of observations is ", as.numeric(estim.t[estim.t$estim==min(as.numeric(estim.t$estim)),]$l), sep=" "))
# print("##############################")
# typeof(estim.t$estim)


##################################
###----- Report generation ----###
##################################
out.f <- paste("Report_", Sys.time(), ".pdf", sep='')
rmarkdown::render("ooiReportGenerating.Rmd", output_dir = out.folder, #./scripts/ooiReportGenerating.Rmd
                  output_file =  out.f)# Sys.Date()
print('Report was generated!', quote = F)

# #Poison procces
# lambda <- 1
# time.t <- 50
# x <-cumsum(rexp(time.t, lambda)) #cummulativ sum of The Exponential Distribution density random variable. 
# y <- cumsum(c(0,rep(1,lambda*time.t))) #cumsum(c(0:time.t))
# plot(stepfun(x,y),xlim = c(0,10),do.points = F)
# axis(1, at=c(0:50))

# now it's sufficient to have steps moments, markov moments
#cumsum(rexp(time.t, lambda)) #cummulativ sum of The Exponential Distribution density random variable. 

# ```{r results='asis', echo=FALSE}
# cat("Here are some dot points\n\n")
# cat("Shigella")
# ```
