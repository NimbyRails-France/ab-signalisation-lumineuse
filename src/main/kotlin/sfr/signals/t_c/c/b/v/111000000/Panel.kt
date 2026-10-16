package sfr.signals.t_c.c.b.v.`111000000`

import nimby.*
import sfr.signals.common.bal.BalPanel

internal object Panel {
    // Nouvelle clé : l'ancien réglage automaticOpening n'est pas inversé.
    val returnToSemaphore = Checkbox("returnToSemaphore", tr("signal.t_c.c.b.v.111000000.mode"),
        tr("signal.t_c.c.b.v.111000000.mode.help"), defaultValue = false)
    val forceClosed = Checkbox("forceClosed", tr("signal.t_c.c.b.v.111000000.force"),
        tr("signal.t_c.c.b.v.111000000.force.help"), visibleWhen = "returnToSemaphore")
    val checkboxes = BalPanel.checkboxes + listOf(returnToSemaphore, forceClosed)
    val workBlocks = BalPanel.workBlocks
}
