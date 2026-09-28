package sfr.signals.bal

import nimby.tr

import nimby.Checkbox
import nimby.NumberSetting

/** Le modèle BAL est toujours calculé ; seules ses variantes sont configurables. */
internal object BalPanel {
    val workBlocks = NumberSetting("workBlocks", tr("bal.workBlocks"), maximum = 64, visibleWhen = "greenFlashWork")
    val checkboxes = listOf(
        Checkbox("greenFlashBlock", tr("bal.greenBlock"), tr("bal.greenBlock.help")),
        Checkbox("greenFlashWork", tr("bal.greenWork"), tr("bal.greenWork.help")),
        Checkbox("yellowFlashEnabled", tr("bal.yellow"), tr("bal.yellow.help")),
        Checkbox("redFlashEnabled", tr("bal.red"), tr("bal.red.help"))
    )
}
