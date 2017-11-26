rm(list=ls(all=TRUE)); gc()
#current.directory <- "/home/vera/agent_resistome/microbiota-resistome/out"
rootDir <- dirname(dirname(rstudioapi::getActiveDocumentContext()$path))
setwd(rootDir)
library("testthat")
source("scripts/functions_for_plots.R")
#source("src/functions_for_plots.R")
#source("/home/vera/agent_resistome/microbiota-resistome/src/functions_for_plots.R")
results <- read.table("out/log.txt", header = T)
results <- results[order(results$Ticks),]
results1 <- read.table("plots/no effects/log.txt", header = T)
results2 <- read.table("plots/wrong_treatment_2/log.txt", header = T)
results3 <- read.table("plots/microbiota_resist_2/log.txt", header = T)
results4 <- read.table("plots/both_2/log.txt", header = T)

results <- read.table("out/simulations/buf_18_04.txt", header = T)
results <- read.table("out/simulations/buftable_14_06_3ver.txt", header = T)
results <- read.table("out/simulations/buftable_15_06.txt", header = T)

tests(results) #equal or not some features
tests(results1)
tests(results2)
tests(results3)
tests(results4)

modif.results <- data.frame(results$Ticks)
modif.results$InfectedPerson <- results$InfectedPersonsInTown + results$InfectedPersonsInHospital
modif.results$InfectedPersonsWithResistantPathogen <- results$AvPathResistance*(results$InfectedPersonsInTown+results$InfectedPersonsInHospital)
modif.results$HospitalisedPersonsWithPathogen <- results$InfectedPersonsInHospital # ?????? toli eto ???????
modif.results$HospitalisedPersonsWithOtherProblems <-  results$HealthyPersonsInHospital
modif.results[is.na(modif.results)] <- 0
# file.create("out/simulations/buftable_14_06_3ver_modif.txt")
# write.table(modif.results, file="out/simulations/buftable_14_06_3ver_modif.txt", sep="\t", append=F, row.names=F, col.names=T, quote=F)



par(mfrow =c(1,1))
#number_of_people(results,0,17000, save = F, "Shigella")
number_of_people(results,0,350, save = F, inp.title = "Shigella")

###################################
###--- Area plot for each AB ---###
###--------- agent state -------###
###################################
pdf(file="data/plots/agent_abund_area_plots.pdf")#, height=3.5, width=5)
#par(xpd=T, mar=par()$mar+c(8,1,1,5))
number_of_people_area_plot(results, inp.title="Shigella")








#####################################
####-------- I dont use it -------###
#####################################
par(mfrow =c(2,2))
number_of_people(results1,0,17000,"plots/no effects",save = F, "а)")
number_of_people(results2,0,30000, "plots/wrong_treatment_2", save = F, "б)")
number_of_people(results3,0, 28000, "plots/microbiota_resist_2/", save = F, "в)")
number_of_people(results4, 0, 30000, "plots/both_2", save =F , "г)")

# rate_plots(results1, "plots/no effects", save = F)
# rate_plots(results2, "plots/wrong_treatment_2", save = F)
# rate_plots(results3, "plots/microbiota_resist_2/", save = F)
# rate_plots(results4, "plots/both_2", save = F)

par(mfrow = c(4,1))
plot_mr(results1,"а)")
plot_mr(results2,"б)")
plot_mr(results3,"в)")
plot_mr(results4,"г)")

par(mfrow =c(1,1))
color = c("black","orange","darkgreen", "darkblue")
plot(results1$Ticks, results1$AvMicResistance, 
     ylim = c(0,max(results1$AvMicResistance, results2$AvMicResistance,
                    results3$AvMicResistance, results4$AvMicResistance)), 
     xlab = "время, дни", ylab = "",type = "l", col = color[1])
lines(results2$Ticks, results2$AvMicResistance, col = color[2])
lines(results3$Ticks, results3$AvMicResistance, col = color[3])
lines(results4$Ticks, results4$AvMicResistance, col = color[4])
legend(3000,0.02,legend = c("а)","б)","в)","г)"), lty = c("solid","solid","solid","solid"), col = color)

par(mfrow = c(4,1))
plot_pr(results1,"а)")
plot_pr(results2,"б)")
plot_pr(results3,"в)")
plot_pr(results4,"г)")

par(mfrow =c(1,1))
color = c("black","orange","darkgreen", "darkblue")
plot(results1$Ticks, results1$AvPathResistance, ylim = c(0,1), 
     xlab = "время, дни", ylab = "",type = "l", col = color[1])
lines(results2$Ticks, results2$AvPathResistance, col = color[2])
lines(results3$Ticks, results3$AvPathResistance, col = color[3])
lines(results4$Ticks, results4$AvPathResistance, col = color[4])
legend(0,1,legend = c("а)","б)","в)","г)"), lty = c("solid","solid","solid","solid"), col = color)

par(mfrow = c(4,1))
plot_ip(results1,"а)")
plot_ip(results2,"б)")
plot_ip(results3,"в)")
plot_ip(results4,"г)")

par(mfrow =c(1,1))
color = c("black","orange","darkgreen", "darkblue")
plot(results1$Ticks, results1$pGetInfectedTown, 
     ylim = c(0,max(results1$pGetInfectedTown, results2$pGetInfectedTown,
                    results3$pGetInfectedTown, results4$pGetInfectedTown)), 
     xlab = "время, дни", ylab = "",type = "l", col = color[1])
lines(results2$Ticks, results2$pGetInfectedTown, col = color[2])
lines(results3$Ticks, results3$pGetInfectedTown, col = color[3])
lines(results4$Ticks, results4$pGetInfectedTown, col = color[4])
legend(3000,0.002,legend = c("а)","б)","в)","г)"), lty = c("solid","solid","solid","solid"), col = color)

