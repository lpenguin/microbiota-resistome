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
plot(estim.t$l, estim.t$estim, type = "o", main= paste("Opt.intens =", as.numeric(estim.t[estim.t$estim==min(as.numeric(estim.t$estim)),]$l), ",  sigma^2 =", sigma.sq, sep=" "))
min(as.numeric(estim.t$estim))
print("##############################")
print("#### Estimation of optimal ###")
print("### observations intensity ###")
print("##############################")
print(paste("Optimal intensity of observations is ", as.numeric(estim.t[estim.t$estim==min(as.numeric(estim.t$estim)),]$l), sep=" "))
print("##############################")
typeof(estim.t$estim)


# #Poison procces
# lambda <- 1
# time.t <- 50
# x <-cumsum(rexp(time.t, lambda)) #cummulativ sum of The Exponential Distribution density random variable. 
# y <- cumsum(c(0,rep(1,lambda*time.t))) #cumsum(c(0:time.t))
# plot(stepfun(x,y),xlim = c(0,10),do.points = F)
# axis(1, at=c(0:50))

# now it's sufficient to have steps moments, markov moments
cumsum(rexp(time.t, lambda)) #cummulativ sum of The Exponential Distribution density random variable. 


