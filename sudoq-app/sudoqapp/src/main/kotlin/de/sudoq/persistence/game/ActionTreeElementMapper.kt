package de.sudoq.persistence.game

import de.sudoq.model.actionTree.ActionTreeElement

object ActionTreeElementMapper {

    fun toBE(actionTreeElement: ActionTreeElement): ActionTreeElementBE {
        val ateBE = ActionTreeElementBE(
            actionTreeElement.id,
            actionTreeElement.action,
            actionTreeElement.parent?.id,
            actionTreeElement.isMarked,
            actionTreeElement.isMistake,
            actionTreeElement.isCorrect)

        return ateBE
    }

    fun fromBE(actionTreeElementBE: ActionTreeElementBE): ActionTreeElement {
        TODO("currently not needed")
    }
}