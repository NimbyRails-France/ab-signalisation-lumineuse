package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*
import sfr.signals.common.bal.BalPanel

internal object Panel {
    val forceClosed = Checkbox("forceClosed", tr("signal.t_c.c.b.v.111000000.force"), tr("signal.t_c.c.b.v.111000000.force.help"))
    val checkboxes = BalPanel.checkboxes + forceClosed
    val workBlocks = BalPanel.workBlocks
}
