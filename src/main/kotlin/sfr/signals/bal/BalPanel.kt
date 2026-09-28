package sfr.signals.bal

import nimby.tr

import nimby.Checkbox

/** Le modèle BAL est toujours calculé ; seules ses variantes sont configurables. */
internal object BalPanel {
    val checkboxes = listOf(
        Checkbox("greenFlashBlock", tr("bal.greenBlock"), tr("bal.greenBlock.help")),
        Checkbox("greenFlashWork", tr("bal.greenWork"), tr("bal.greenWork.help")),
        Checkbox("yellowFlashEnabled", tr("bal.yellow"), tr("bal.yellow.help")),
        Checkbox("redFlashEnabled", tr("bal.red"), tr("bal.red.help"))
    )
}
