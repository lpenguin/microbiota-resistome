rm(list=ls(all=TRUE)); gc()
#current.directory <- "/home/vera/agent_resistome/microbiota-resistome/out"
rootDir <- dirname(dirname(rstudioapi::getActiveDocumentContext()$path))
setwd(rootDir)
library("reshape")


trans.log <- read.table("out/log/transLog_6_06.txt", header = T, fill = T, comment.char = '', quote = '')
trans.log$path <- with(data = trans.log, paste(Ticks, TransFromClass, TransToClass, sep ="-->"))

trans.log <- aggregate(data = trans.log, . ~ PersonId, FUN = )