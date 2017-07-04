tests <- function(results){
  expect_equal(results$InfectedPersonsInTown,results$IncPeriodPersonsInTown+results$AntibioticTreatedPersonsInTown+
                 results$IncPeriodPersonsInTown2 +results$AntibioticTreatedPersonsInTown2)
  expect_equal(results$InfectedPersonsInTown+results$HealthyPersonsInTown +results$InfectedPersonsInHospital +
                 results$HealthyPersonsInHospital,
               rep(results$InfectedPersonsInTown[1]+results$HealthyPersonsInTown[1] +results$InfectedPersonsInHospital[1] +
                     results$HealthyPersonsInHospital[1],
                   nrow(results)))
  expect_equal(results$AvMicResistance<=1 & results$AvMicResistance>=0,rep(T,nrow(results)))
  expect_equal(results$AvPathResistance[!is.na(results$AvPathResistance)]<=1 & 
                 results$AvPathResistance[!is.na(results$AvPathResistance)]>=0,
               rep(T,length(results$AvPathResistance[!is.nan(results$AvPathResistance)])))
}
number_of_people <- function(results,  legend_x, legend_y, folder = "plots/tmp",save, inp.title){
  filename <- paste0(folder,"/number_of_people.png")
  if (save == T) png(filename)
 # par(mfrow = c(1,1))
  plot(results$Ticks, results$InfectedPersonsInTown + results$InfectedPersonsInHospital, col = "blue", type = "l",
       xlab = "время, дни", ylab = "количество человек", lwd=2,main = inp.title,
       ylim = c(0, 50+2*max(results$InfectedPersonsInHosp + results$InfectedPersonsInTown,
                       results$HealthyPersonsInHospital)))
  lines(results$Ticks,results$AvPathResistance*(results$InfectedPersonsInTown+results$InfectedPersonsInHospital),
        col = "red", lwd = 2)
  lines(results$Ticks, results$InfectedPersonsInHospital, col = "green", lwd=2)
  lines(results$Ticks, results$HealthyPersonsInHospital, col = "purple", lwd =2)
  legend(legend_x, legend_y, inset=.02, cex=0.5,
         c ("инфицированные", "инфицированнные резистентным патогеном",
            "госпитализированные с данной инфекцией", 
            "находящиеся в больнице по другому поводу"),

         col = c("blue", "red", "green","purple"), lty = c(1,1,1,1), lwd = c(2,2,2,2))
  if (save == T) dev.off()
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