package sfr.tests

import nimby.*
import sfr.FrenchSignalsMod
import sfr.settings.SignalSettings
import sfr.signalling.*
import sfr.rendering.SignalTextures
import sfr.driving.*
import kotlin.math.*

internal fun ter() = Vehicle(160.0/3.6, 1.0, 0.65, 160000.0, 1900000.0, 163200.0, 0.0, 72.36)

internal fun testDrivingModelAndMemory() {
    expect(SignalConstraints.fromSignal(Aspect.YellowFlash,1,0.0,500.0) == null)
    expect(SignalConstraints.fromSignal(Aspect.YellowFlash,1,0.0,500.0,900.0)!!.beginM == 900.0)
    expect(SignalConstraints.fromSignal(Aspect.GreenFlash,1,0.0,1500.0)!!.beginM == 1500.0)
    expect(SignalConstraints.fromSignal(Aspect.RedFlash,1,0.0)!!.speedMps == 15.0/3.6)
    expect(SignalConstraints.fromSignal(Aspect.A,1,0.0,700.0,announcedTarget=Aspect.RedFlash)!!.speedMps == 15.0/3.6)
    expect(SignalConstraints.fromSignal(Aspect.A,1,0.0,700.0,announcedTarget=Aspect.Unknown) == null)
    expect(SignalConstraints.fromSignal(Aspect.S,0,0.0) == null)
    expect(SignalConstraints.fromSignal(Aspect.S,1,Double.NaN) == null)
    val v=ter(); val s=DrivingSettings()
    var i=DrivingInput(0.0,30.0,200.0/3.6,true,true)
    val stop=Constraint(1,1000.0,100000.0,0.0,false)
    val p=DrivingModel.plan(v,s,i,listOf(stop))
    expect(p.available && p.speedCeilingMps < v.maxSpeedMps)
    val required=p.speedCeilingMps*s.responseSeconds+p.speedCeilingMps*p.speedCeilingMps/(2*p.serviceDecelerationMps2)+s.marginM
    expect(abs(required-1000) < 1e-7)
    expect(DrivingModel.plan(v.copy(extraMassKg=40000.0),s,i,listOf(stop)).speedCeilingMps < p.speedCeilingMps)
    expect(!DrivingModel.plan(v,s,i.copy(fresh=false),listOf(stop)).available)
    expect(!DrivingModel.plan(v.copy(emptyMassKg=Double.NaN),s,i,emptyList()).available)
    expect(!DrivingModel.plan(v,s,i.copy(onSight=true),emptyList()).available)
    expect(DrivingModel.plan(v,s,i.copy(onSight=true,visibleClearM=12.0),emptyList()).speedCeilingMps < 30.0/3.6)
    expect(DrivingModel.plan(v,s,i.copy(onSight=true,visibleClearM=0.0),emptyList()).speedCeilingMps == 0.0)
    expect(!DrivingModel.plan(v,s,i,listOf(stop.copy(source=0))).available)
    expect(!DrivingModel.plan(v,s.copy(brakeUse=1.1),i,emptyList()).available)
    val m=DrivingMemory(); m.reset(1); m.announceStop(20,1000.0);m.observeAnnouncedSignal(20,true)
    expect(abs(DrivingModel.plan(v,s,i.copy(headM=999.0),m.constraints(999.0,v.lengthM)).speedCeilingMps-30.0/3.6)<1e-8)
    expect(DrivingModel.plan(v,s,i.copy(headM=1100.0),m.constraints(1100.0,v.lengthM)).speedCeilingMps <= 30.0/3.6)
    m.setRestriction(Constraint(99,900.0,2000.0,20.0/3.6));m.passSignal(20)
    expect(DrivingModel.plan(v,s,i.copy(headM=1100.0),m.constraints(1100.0,v.lengthM)).speedCeilingMps <= 20.0/3.6)
    m.removeRestriction(99);m.receiveGreenFlash(500.0);m.receiveGreenFlash(1000.0)
    expect(m.constraints(400.0,200.0).single().beginM == 500.0)
    m.passGreenExit(1500.0)
    expect(m.constraints(1700.0,200.0).isNotEmpty())
    expect(m.constraints(1700.001,200.0).isEmpty())
    m.announceStop(30,1900.0,10.0/3.6);m.observeAnnouncedSignal(30,true);m.announceStop(30,1900.0,20.0/3.6)
    expect(m.constraints(1800.0,200.0).single().speedMps == 10.0/3.6)
    m.reset(2);expect(m.constraints(0.0,200.0).isEmpty())
    rejected { m.announceStop(0,0.0) };rejected { m.receiveGreenFlash(Double.NaN) }
    rejected { m.constraints(0.0,0.0) };rejected { m.setRestriction(stop.copy(endM=-1.0)) }
    i=DrivingInput(0.0,40.0,160.0/3.6,true,true)
    val target=Constraint(2,1800.0,100000.0,0.0,false)
    var stopped=false
    for(step in 0 until 10000) {
        val plan=DrivingModel.plan(v,s,i,listOf(target));expect(plan.available)
        val speed=max(0.0,i.speedMps+plan.accelerationMps2*0.05)
        i=i.copy(headM=i.headM+(i.speedMps+speed)*0.025,speedMps=speed)
        expect(i.headM<=target.beginM)
        if(speed==0.0 && plan.speedCeilingMps<0.1){stopped=true;break}
    }
    expect(stopped)
}
