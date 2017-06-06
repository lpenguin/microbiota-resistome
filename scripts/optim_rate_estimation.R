rm(list=ls(all=TRUE)); gc()
#current.directory <- "/home/vera/agent_resistome/microbiota-resistome/out"
rootDir <- dirname(dirname(rstudioapi::getActiveDocumentContext()$path))
setwd(rootDir)
library("reshape")
library(zoo)

dt <- data.frame()
for (cfile in list.files('out/simulations/May_22_time_16_42/', pattern = '.txt' )) {
  cur.dt <- read.table(sprintf("out/simulations/May_22_time_16_42/%s", cfile), header = T, fill = T,
                       comment.char = '', quote = '')
  cur.dt$simulation <- cfile
  dt <- rbind(dt, cur.dt)
}
dt$simulation <- gsub('(.*)\\.txt', '\\1', dt$simulation)
dt$Ticks <- dt$Ticks+1
dt$AllInfectedPer <- with(dt, InfectedPersonsInTown + InfectedPersonsInHospital)
head(dt)


time.t <- max(dt$Ticks)
delta <- 1
estim.t <- NULL
all.lambda <- sapply(seq(0.1, 5, by=.1) , function(lambda) { # or so seq(1/time.t, 5, by=1/time.t)
  matog <- 0
  sapply(unique(dt$simulation) , function(sim) { # or so seq(1/time.t, 5, by=1/time.t)
    # lambda <- 1
    # sim <- "rep100"
    infect.p <- dt[dt$simulation==sim,"HealthyPersonsInHospital"] #infeected people
    step.m <- cumsum(rexp(time.t, lambda)) # steps moments, markov moments
    step.m <- step.m[step.m<=100&step.m>=1] 
    observ <- infect.p[trunc(step.m)] #I dont like that we dont consider 0-step 
    observ.full <- unique(na.locf(merge(data.frame(val=observ, time=trunc(step.m)),data.frame(time=1:time.t), all.y = T), fromLast = F))
    observ.full[is.na(observ.full)] <- 0
    observ.full$real.v <- infect.p 
    observ.full$diff.sqr <- (observ.full$real.v - observ.full$val)^2
    
    # observ.full$is.na <- as.numeric(is.na(observ.full$val))
    # observ.full2 <- na.locf(observ.full, fromLast = F)
    
    # plot(1:time.t, infect.p, col="red", type = "o", lwd=3)
    # lines(trunc(step.m), observ, col="green",lwd=1)
    # 
    matog <- matog + sum(observ.full$diff.sqr)*delta 
  })
  matog <- 1*lambda + (matog/100) # 1- constA, 100- kol trajectories
  estim.t <- rbind(estim.t, data.frame(l=lambda, estim=matog))
})


# #Poison procces
# lambda <- 1
# time.t <- 50
# x <-cumsum(rexp(time.t, lambda)) #cummulativ sum of The Exponential Distribution density random variable. 
# y <- cumsum(c(0,rep(1,lambda*time.t))) #cumsum(c(0:time.t))
# plot(stepfun(x,y),xlim = c(0,10),do.points = F)
# axis(1, at=c(0:50))

# now it's sufficient to have steps moments, markov moments
cumsum(rexp(time.t, lambda)) #cummulativ sum of The Exponential Distribution density random variable. 


