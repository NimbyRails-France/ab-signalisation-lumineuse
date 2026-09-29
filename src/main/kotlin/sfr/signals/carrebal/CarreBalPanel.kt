package sfr.signals.carrebal

import nimby.*
import sfr.signals.common.bal.BalPanel

internal object CarreBalPanel {
    val forceClosed = Checkbox("forceClosed", tr("carreBal.force"), tr("carreBal.force.help"))
    val checkboxes = BalPanel.checkboxes + forceClosed
    val workBlocks = BalPanel.workBlocks
}
