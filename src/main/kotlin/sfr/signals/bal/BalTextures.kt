package sfr.signals.bal

/** Rendu du BAL et phase de clignotement à partir du temps simulé. */
internal object BalTextures {
    fun path(aspect: BalAspect, simulationMs: Long, halfPeriodMs: Long = 500): String {
        val folder="imgs/ca/sem_bal/"
        if(simulationMs<0 || halfPeriodMs !in 100..10000) return folder+"xx.svg"
        val lit=(simulationMs/halfPeriodMs)%2==0L
        val file=when(aspect) {
            BalAspect.Unknown->"xx";BalAspect.VL->"tex02";BalAspect.A->"tex03";BalAspect.S->"tex04"
            BalAspect.GreenFlash->if(lit)"tex05" else "tex06"
            BalAspect.YellowFlash->if(lit)"tex07" else "tex08"
            BalAspect.RedFlash->if(lit)"tex09" else "tex10"
        }
        return "$folder$file.svg"
    }
}
