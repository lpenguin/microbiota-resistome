rm(list=ls(all=TRUE)); gc()
#current.directory <- "/home/vera/agent_resistome/microbiota-resistome/out"
rootDir <- dirname(dirname(rstudioapi::getActiveDocumentContext()$path))
setwd(rootDir)
library("reshape")
library("ggplot2")
library(RColorBrewer)
library(gtable)
library(grid)
library(gridExtra)

trans.log <- read.table("out/log/transLog_14_06_2ver.txt", header = T, fill = T, comment.char = '', quote = '')
trans.log$path <- with(data = trans.log, paste(Ticks, TransFromClass, TransToClass, sep ="-->"))
trans.log$path.2ver <- with(data = trans.log, paste(TransFromClass, TransToClass, sep ="-->"))
trans.log$buf <- 1

trans.summary <- cast(trans.log[!is.na(trans.log$TransFromClass),], ~ path.2ver, sum, value = 'buf')
# or this method http://sickel.net/blogg/?p=688
# install.packages('plotrix', dependencies = TRUE)
require(plotrix)
pdf("out/log/trans_variety.pdf", width=10, height=10)
par(mar=c(18,7,1,1),xpd=TRUE)

gap.barplot( as.matrix(trans.summary[,-1]), 
             beside = TRUE, 
             gap=c(150,1750), 
             ytics=c(0,50,100,150,1750,1800,1850,1900, 1950,2000),
             xaxt='n')
staxlab(at=1:11, labels = colnames(trans.summary)[-1],srt=65)
dev.off()
#text(cex=1, x=x-.25, y=-1.25, colnames(trans.summary)[-1], xpd=TRUE, srt=45)

#axis(1, at=1:11, lab=colnames(trans.summary)[-1], )             


trans.summary.2ver <- cast(trans.log[!is.na(trans.log$TransFromClass),], Ticks ~ path.2ver, sum, value = 'buf') # why we see that on 1 tick townHealthyPersons-->townIncPerPersons isnot empty
head(trans.summary.2ver)
# plot(trans.summary.2ver$Ticks, trans.summary.2ver$`healthyHospPeople-->townHealthyPersons`, type = "l", 
#      ylab='dispersion', xlab='mean of normalized counts')
# #xg = 10^seq( -.5, 5, length.out=300 )
# 
# lines( trans.summary.2ver$Ticks, trans.summary.2ver$`hospAntTrPersons-->townHealthyPersons`, col='red')
# # to add aother lines
#other way for plotting 
col.pall <- brewer.pal(12,"Paired")#c("#1f78b4","#C54E6D", "#009380","#ff7f00")
par(mar=c(4,4,1,17),xpd=TRUE)
plot(trans.summary.2ver[,1],trans.summary.2ver[,2], type="l", col=col.pall[1],lwd=2, ylim=c(0,max(trans.summary.2ver[,-1])),xlim=c(0,max(trans.summary.2ver[,1])+1),axes=FALSE, ann=FALSE)
# segments(c(1:(dim(t.sympt.agg)[1]-1)),t.sympt.agg$visit3-t.sd$visit3, c(1:(dim(t.sympt.agg)[1]-1)), t.sympt.agg$visit3+t.sd$visit3)
# epsilon <- 0.02
# segments(c(1:(dim(t.sympt.agg)[1]-1))-epsilon, t.sympt.agg$visit3-t.sd$visit3, c(1:(dim(t.sympt.agg)[1]-1))+epsilon, t.sympt.agg$visit3-t.sd$visit3)
# segments(c(1:(dim(t.sympt.agg)[1]-1))-epsilon, t.sympt.agg$visit3+t.sd$visit3, c(1:(dim(t.sympt.agg)[1]-1))+epsilon, t.sympt.agg$visit3+t.sd$visit3)
lapply(c(3:length(trans.summary.2ver)), FUN = function(num){
  lines(trans.summary.2ver[,1],trans.summary.2ver[,num], type="l", lty=1,lwd=2, col=col.pall[num-1])
})
title(main="Popularity of agents transitions", col.main="black", font.main=2) # main title

axis(1, at=seq(0,max(trans.summary.2ver[,1])+length(trans.summary.2ver[,1])/10,by=length(trans.summary.2ver[,1])/10),col="black", col.axis="black") # Make x axis using stage labels
#text(seq(1.4,length(n.bact)+1,by=1), par("usr")[3]-0.3, srt = 60, adj= 1, xpd = TRUE, labels = n.bact, cex=0.7,col="black")
axis(2, las=1, at=seq(0,max(trans.summary.2ver[,-1])+round(max(trans.summary.2ver[,-1])/10),by=round(max(trans.summary.2ver[,-1])/10)), col="black", col.axis="black") # Make y axis

# Label the x and y axes with dark green text
title(ylab="Transitions number per tick", col.lab="black")
title(xlab="Ticks", col.lab="black")

legend(max(trans.summary.2ver[,1]), round(max(trans.summary.2ver[,-1])), colnames(trans.summary.2ver)[-1], cex=0.8, bty = "n", col=col.pall, text.col=col.pall, pch=19, lty=1, lwd=1)
dev.off()



trans_number_area_plot <- function(res.t, inp.title, col.pall){
  # res.t <- trans.summary.2ver
  # inp.title <- "Shigella"
  #col.pall <- brewer.pal(12,"Paired")

  pl.table <- res.t
  pl.table.m <- melt(pl.table, id="Ticks")
  rownames(pl.table.m) <- NULL
  # head(pl.table.m)

  proportions_fun=function(vec){
    # print(as.numeric(vec[3]))
    # print(vec)
    # print(sum(pl.table.m$value[pl.table.m$Ticks==vec[1]]))
    as.numeric(vec[2]) / sum(pl.table.m$value[pl.table.m$Ticks==as.numeric(vec[1])])
  }

  # bact.mean.m <- melt(bact.mean, id=c("antibiotic", "visit.No"))
  # bact.mean.all.m <- melt(bact.mean.all, id="visit.No")
  #
  # proportions_fun=function(vec){ as.numeric(vec[3]) / sum(data$value[data$visit.No==vec[1]]) }
  #
  #head(results)

  pl.table.m$prop=apply(pl.table.m, 1 , proportions_fun)
  colnames(pl.table.m) <- c("Ticks","trans_val","trans_type", "prop")
  pl <- ggplot(pl.table.m, aes(x=Ticks, y=prop, fill=trans_type)) +
    ylab("Proportion from the total transitions per current tick")+
    ggtitle(paste("Popularity of agents transitions for",inp.title, sep=" ")) +
    theme(plot.title = element_text(hjust = 0.5))+
    geom_area(alpha=0.6 , size=1) +
    scale_fill_manual(values=col.pall, name="Trans type"
                      # breaks=c("infected.a","infected.resist.path","gosp.with.path","gosp.with.no.path"),
                      # labels=c("инфицированные", "инфицированнные резистентным патогеном",
                      #          "госпитализированные с данной инфекцией",
                      #          "находящиеся в больнице по другому поводу")
                      )#+ #, colour="black"
  #geom_bar(stat="identity", width = 0.1, colour="black")
  grid.arrange(pl)

}
trans_number_area_plot(trans.summary.2ver, "Shigella", brewer.pal(12,"Paired"))

trans.log.agg <- aggregate(data = trans.log, path ~ PersonId, FUN = function(x){paste(x, collapse=",")})
trans.log.agg <- trans.log.agg[order(as.numeric(gsub('id\\_(.*)', '\\1', trans.log.agg$PersonId))),]
# file.create("out/log/transLog_14_06_aggreg.txt")
# write.table(trans.log.agg, file="out/log/transLog_14_06_aggreg.txt", sep="\t", append=F, row.names=F, col.names=T, quote=F)

# for nikita 
trans.log <- read.table("out/log/transLog_14_06_3ver.txt", header = T, fill = T, comment.char = '', quote = '')
trans.log <- trans.log[!is.na(trans.log$TransFromClass),]
# file.create("out/log/transLog_NP.txt")
# write.table(trans.log, file="out/log/transLog_NP.txt", sep="\t", append=F, row.names=F, col.names=T, quote=F)


unique(trans.log$TransFromClass)
state.vol <- data.frame("townHealthyPersons"=10000, "townIncPerPersons"=50,"townAntTrPersons2"=0,"healthyHospPeople"=0,"townAntTrPersons"=0,"townIncPerPersons2"=0,"hospAntTrPersons"=0)
# file.create("out/log/state_volume.txt")
# write.table(state.vol, file="out/log/state_volume.txt", sep="\t", append=F, row.names=F, col.names=T, quote=F)
