rm(list=ls(all=TRUE)); gc()
#current.directory <- "/home/vera/agent_resistome/microbiota-resistome/out"
rootDir <- dirname(dirname(rstudioapi::getActiveDocumentContext()$path))
setwd(rootDir)
library("reshape")


trans.log <- read.table("out/log/transLog_14_06_2ver.txt", header = T, fill = T, comment.char = '', quote = '')
trans.log$path <- with(data = trans.log, paste(Ticks, TransFromClass, TransToClass, sep ="-->"))

trans.log.agg <- aggregate(data = trans.log, path ~ PersonId, FUN = function(x){paste(x, collapse=",")})
trans.log.agg <- trans.log.agg[order(as.numeric(gsub('id\\_(.*)', '\\1', trans.log.agg$PersonId))),]
# file.create("out/log/transLog_14_06_aggreg.txt")
# write.table(trans.log.agg, file="out/log/transLog_14_06_aggreg.txt", sep="\t", append=F, row.names=F, col.names=T, quote=F)
