#!/usr/bin/env Rscript
args = commandArgs(trailingOnly=TRUE)
# i run it so 
#' Rscript --vanilla modelPlots.R ../out/simulations/buftable_15_06.txt 
#' ../out/plots/Aug16/ ../out/log/transLog_14_06_2ver.txt Shigella


# rootDir <- dirname(rstudioapi::getActiveDocumentContext()$path)
# setwd(rootDir)
source("functionsSet.R")

# args <- c("../out/simulations/buftable_15_06.txt","../out/plots/Aug16/","../out/log/transLog_14_06_2ver.txt", "Shigella")

print("####################")
print("##### R script #####")
print("####################")
##################################
###--- Test of input model  ---###
###-------- parametres --------###
##################################

# if there are null arguments, than return an error
if (length(args)==0) {
  stop("At least one argument must be supplied (input file).n", call.=FALSE)
} 

for (i in 1:length(args)) {
  print(paste(i,"argument =",args[i], sep=" "), quote = F)
}

###---- Abundance table ----###
abund.log <- read.table(args[1], header = T, fill = T, comment.char = '', quote = '', stringsAsFactors = F)
# riddle a correctness of tanble 
# if colnames of abundances table isn't correct, than return an error
if (sum(as.numeric(colnames(abund.log)!=c("Ticks","HealthyPersonsInTown","InfectedPersonsInTown","IncPeriodPersonsInTown","IncPeriodPersonsInTown2","AntibioticTreatedPersonsInTown","AntibioticTreatedPersonsInTown2","InfectedPersonsInHospital","HealthyPersonsInHospital","pGetInfectedTown","AvMicResistance","AvPathResistance")))>0) {
  stop("Table which was supplied (input file).n as 1st argument is not correct", call.=FALSE)
}

###---- Output directory ----###
out.folder <- args[2]

###---- Transitions table ----###
trans.log <- read.table(args[3], header = T, fill = T, comment.char = '', quote = '', stringsAsFactors = F)
# riddle a correctness of tanble 
# if colnames of transitions table isn't correct, than return an error
if (sum(as.numeric(colnames(trans.log)!=c("Ticks","PersonId","TransFromClass","TransToClass")))>0) {
  stop("Table which was supplied (input file).n as 3rd argument is not correct", call.=FALSE)
}
trans.note <- read.table(paste(dirname(getwd()), "/resources/transTypes", sep = ""), header = T, fill = T, comment.char = '', quote = '', stringsAsFactors = F, sep = "\t")
rownames(trans.note) <- trans.note$type

###---- Name of pathogen ----###
name.path <- args[4]


##################################
###----- Data preprocesing ----###
##################################
trans.log$path <- with(data = trans.log, paste(TransFromClass, TransToClass, sep ="-->"))
trans.log$var <- 1

trans.summary <- cast(trans.log[!is.na(trans.log$TransFromClass),], Ticks ~ path, sum, value = 'var') # why we see that on 1 tick townHealthyPersons-->townIncPerPersons isnot empty


##################################
###------------ Plots ---------###
##################################
col.pall.l <- c("#a6cee3","#1f78b4","#b2df8a","#33a02c","#fb9a99","#fb6a4a","#fdbf6f","#ff7f00","#cab2d6","#6a3d9a","#ffd92f","#fc4e2a")
col.pall.s <- c("#999999", "#E69F00", "#56B4E9", "#33a02c")

###---- line plot of abund table ----###
pdf(file=paste(out.folder,"/tmp/plot1.pdf", sep=""))
number_of_people(abund.log,0,350, save = F, inp.title = name.path, col.pal = col.pall.s)
dev.off()
###---- area plot of abund table ----###
pdf(file=paste(out.folder,"/tmp/plot2.pdf", sep=""))
number_of_people_area_plot(abund.log, inp.title=name.path, col.pall.s)
dev.off()

###---- line plot of trans table ----###
pdf(file=paste(out.folder,"/tmp/plot3.pdf", sep=""))
trans_line_plot(trans.summary, col.p = col.pall.l, note.t = trans.note, inp.title = name.path)
dev.off()

###---- area plot of trans table ----###
pdf(file=paste(out.folder,"/tmp/plot4.pdf", sep=""))
trans_number_area_plot(trans.summary, name.path, col.pall.l, note.t = trans.note)
dev.off()


##################################
###----- Report generation ----###
##################################
out.f <- paste("Report_", Sys.time(), ".pdf", sep='')
rmarkdown::render("reportGenerating.Rmd", output_dir = out.folder,
                  output_file =  out.f)# Sys.Date()
print('Report was generated!', quote = F)
