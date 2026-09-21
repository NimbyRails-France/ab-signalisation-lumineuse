package sfr.tests

import nimby.*
import sfr.FrenchSignalsMod
import sfr.settings.SignalSettings
import sfr.signalling.*
import sfr.rendering.SignalTextures
import sfr.driving.*
import kotlin.math.*

internal fun testCppEquivalence() {
    var hash=14695981039346656037uL
    for (mask in 0 until 1024) {
        fun bit(n: Int) = mask and (1 shl n) != 0
        val s=SignalSettings(bit(0),bit(1),bit(2),bit(3),bit(4),bit(5),bit(6),bit(7),bit(8),bit(9))
        for(flags in 0 until 32) for(block in Occupancy.entries) for(next in Aspect.entries) {
            fun flag(n: Int)=flags and (1 shl n) != 0
            val d=BalRules.evaluate(s,SignalObservation(block,flag(0),flag(1),flag(2),flag(3),flag(4),next))
            hash=(hash xor d.aspect.ordinal.toULong())*1099511628211uL
            hash=(hash xor d.reason.ordinal.toULong())*1099511628211uL
        }
    }
    expect(hash == cppDecisionHash)
    for(n in cppPlans.indices) {
        val v=ter().copy(extraMassKg=(n%7)*10000.0)
        val i=DrivingInput(n*5.0,(n%60).toDouble(),200.0/3.6,n%17!=0,n%19!=0,n%3==0,(n%100).toDouble())
        val c=Constraint(9007199254740993L+n,1000.0,1200.0,(n%4)*5.0,n%2==0)
        val p=DrivingModel.plan(v,DrivingSettings(),i,listOf(c))
        val expected=cppPlans[n]
        expect(p.available == (expected[0] != 0.0))
        expect(abs(p.speedCeilingMps-expected[1]) < 1e-10)
        expect(abs(p.serviceDecelerationMps2-expected[2]) < 1e-10)
        expect(abs(p.accelerationMps2-expected[3]) < 1e-10)
        expect(p.brakingRequired == (expected[4] != 0.0))
        expect(p.limitingSource == cppSources[n])
    }
    println("PASS: équivalence C++ : 786432 décisions et 256 plans de conduite (identifiants 64 bits inclus)")
}
