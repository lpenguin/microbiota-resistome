library(reshape)
library(ggplot2)
library(gridExtra)
library(RColorBrewer)
library(rmarkdown)
library(rstudioapi)

# install.packages("devtools")
# devtools::install_github("rstudio/rmarkdown")

number_of_people <- function(results,  legend_x, legend_y, folder = "plots/tmp",save, inp.title, col.pal){
  filename <- paste0(folder,"/number_of_people.png")
  if (save == T) png(filename)
 # par(mfrow = c(1,1))
  ylim.param <- with(results, max(InfectedPersonsInHospital + InfectedPersonsInTown,HealthyPersonsInHospital))
  plot(results$Ticks, results$InfectedPersonsInTown + results$InfectedPersonsInHospital, col = col.pal[1], type = "l",
       xlab = "Time, days", ylab = "Number of people in states", lwd=2,main = paste("Model states for",inp.title, sep=" "),
       ylim = c(0, 50+1.5*ylim.param))
  #axis(2, at=round(seq(0,ylim.param,by=round(ylim.param/5))),col="black", col.axis="black") # Make x axis using stage labels
  lines(results$Ticks,results$AvPathResistance*(results$InfectedPersonsInTown+results$InfectedPersonsInHospital), # TODO: chage it
        col = col.pal[2], lwd = 2)

  lines(results$Ticks, results$InfectedPersonsInHospital, col = col.pal[3], lwd=2)
  lines(results$Ticks, results$HealthyPersonsInHospital, col = col.pal[4], lwd =2)
  legend(0, 50+1.5*ylim.param, cex=0.9, inset=c(50,50), title = "Agent classes",
         c ("All infected people", "Infected people by resist. pathogen",
            "Hospitalized with current patogen","Hospitalized with other problems"),
         col = col.pal, lty = c(1,1,1,1), lwd = c(2,2,2,2))
  if (save == T) dev.off()
}

number_of_people_area_plot <- function(res.t, inp.title, col.pal){
  # res.t <- results 
  # inp.title <- "Shigella"
  proportions_fun=function(vec){
    as.numeric(vec[2]) / sum(pl.table.m$value[pl.table.m$Ticks==as.numeric(vec[1])])
  }  
  
  pl.table <- res.t
  pl.table$infected.a <- pl.table$InfectedPersonsInTown + pl.table$InfectedPersonsInHospital
  pl.table$infected.resist.path <- pl.table$AvPathResistance*(pl.table$InfectedPersonsInTown+pl.table$InfectedPersonsInHospital)
  pl.table$gosp.with.path <- pl.table$InfectedPersonsInHospital # ?????? toli eto ???????
  pl.table$gosp.with.no.path <-  pl.table$HealthyPersonsInHospital
  pl.table <- pl.table[,c("Ticks","infected.a","infected.resist.path","gosp.with.path","gosp.with.no.path")]
  
  pl.table.m <- melt(pl.table, id="Ticks")[,c("Ticks","value","variable")]

  pl.table.m$prop=apply(pl.table.m, 1 , proportions_fun)
  colnames(pl.table.m) <- c("Ticks", "Agent_amount", "Agent_classes", "prop")
  pl <- ggplot(pl.table.m, aes(x=Ticks, y=prop, fill=Agent_classes)) + 
    ylab("Proportion from the total ill agents")+ xlab("Time, days")+
    ggtitle(paste("Model states for",inp.title, sep=" ")) +
    coord_cartesian(ylim=c(0,1)) +
    theme(plot.title = element_text(hjust = 0.5),axis.title=element_text(size=13),legend.title=element_text(size=13), legend.text=element_text(size=11), legend.position="bottom",legend.direction="vertical")+
    geom_area(alpha=0.6 , size=1) +
    scale_fill_manual(values=col.pal, name="Agent classes",
                       breaks=c("infected.a","infected.resist.path","gosp.with.path","gosp.with.no.path"),
                       labels= c ("All infected people", "Infected people by resist. pathogen",
                                  "Hospitalized with current patogen","Hospitalized with other problems") #c("инфицированные", "инфицированнные резистентным патогеном","госпитализированные с данной инфекцией","находящиеся в больнице по другому поводу")
                      )#+ #, colour="black"
    #geom_bar(stat="identity", width = 0.1, colour="black")
  grid.arrange(pl)
  
}

trans_line_plot <- function(input.t, inp.title, col.p, note.t){
  #layout(cbind(1,2), widths=c(5,5))
  layout(rbind(1,2), heights=c(6,4))
  plot(input.t[,1],input.t[,2], type="l", col=col.p[1],lwd=2, ylim=c(0,max(input.t[,-1])),xlim=c(0,max(input.t[,1])+1), ann=FALSE)
  ll <- lapply(c(3:length(input.t)), FUN = function(num){
    lines(input.t[,1],input.t[,num], type="l", lty=1,lwd=2, col=col.p[num-1])
  })
  title(main=paste("Agents transitions between states for",inp.title,sep=" "), col.main="black", font.main=2) # main title
  
  # Label the x and y axes with dark green text
  title(ylab="Transitions number per day", col.lab="black")
  title(xlab="Time, days", col.lab="black")
  par(mar=c(0, 0, 0, 0))
  plot.new()
  legend("center", note.t[colnames(input.t)[-1],]$note, cex=0.9, bty = "n", col=col.p, pch=15,pt.cex = 2)#, title = "Transition types")
}


trans_number_area_plot <- function(res.t, inp.title, col.pall, note.t){
  # res.t <- trans.summary.2ver
  # inp.title <- "Shigella"
  # col.pall <- brewer.pal(12,"Paired")
  
  proportions_fun=function(vec){
    as.numeric(vec[2]) / sum(pl.table.m$value[pl.table.m$Ticks==as.numeric(vec[1])])
  }  
  
  pl.table <- res.t
  pl.table.m <- melt(pl.table, id="Ticks")
  rownames(pl.table.m) <- NULL

  pl.table.m$prop=apply(pl.table.m, 1 , proportions_fun)
  colnames(pl.table.m) <- c("Ticks","trans_val","trans_type", "prop")
  pl <- ggplot(pl.table.m, aes(x=Ticks, y=prop, fill=trans_type)) +
    ylab("Proportion from the total transitions per current day")+ xlab("Time, days")+
    ggtitle(paste("Agents transitions between states for",inp.title,sep=" ")) +
    theme(plot.title = element_text(hjust = 0.5),axis.title=element_text(size=13),legend.title=element_text(size=13), legend.text=element_text(size=11), legend.position="bottom",legend.direction="vertical")+
    geom_area(alpha=0.6 , size=1) +
    scale_fill_manual(values=col.pall, name="Transitions type",
                      breaks=colnames(pl.table)[-1],
                      labels=note.t[colnames(pl.table)[-1],]$note
    )#+ #, colour="black"
  #geom_bar(stat="identity", width = 0.1, colour="black")
  grid.arrange(pl)
  
}



plot_mr <- function(results, title = "средний уровень резистентности микробиоты") {
  plot(results$Ticks, results$AvMicResistance, type = "l", lwd = 3,
       xlab = "время, дни",ylab = "", main = title, 
       ylim = c(0,max(results$AvMicResistance)))
}
plot_pr <- function(results, title ="доля больных резистентным патогеном среди всех больных" ) {
  plot(results$Ticks, results$AvPathResistance, type = "l", lwd = 3,
       xlab = "время, дни", ylab = "", main = title, ylim = c(0,1))
} 
plot_ip <- function(results, title ="вероятность заразиться здоровому человеку в городе" ){
  plot(results$Ticks, results$pGetInfectedTown, type = "l", lwd = 3,
       xlab = "время, дни", ylab = "", main = title)
}

rate_plots <- function(results, folder = "plots/tmp", save){
  filename <- paste0(folder,"/rates.png")
  if (save == T) png(filename)
  par(mfrow = c(3,1))
  #average microbiome resistance
  plot(results$Ticks, results$AvMicResistance, type = "l", lwd = 3,
       xlab = "время, дни",ylab = "", 
       main = "средний уровень резистентности микробиоты", 
       ylim = c(0,max(results$AvMicResistance)))
  
  #pathogene resistance rate
  plot(results$Ticks, results$AvPathResistance, type = "l", lwd = 3,
       xlab = "время, дни", ylab = "",
       main = "доля больных резистентным патогеном среди всех больных",
       ylim = c(0,1))
  
  #probability to be infected in town
  plot(results$Ticks, results$pGetInfectedTown, type = "l", lwd = 3,
       xlab = "время, дни", ylab = "", 
       main = "вероятность заразиться здоровому человеку в городе")
  if (save == T) dev.off()
}