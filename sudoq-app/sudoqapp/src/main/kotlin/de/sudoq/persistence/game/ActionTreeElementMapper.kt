package de.sudoq.persistence.game

import de.sudoq.model.actionTree.ActionTreeElement

object ActionTreeElementMapper {

    fun toBE(actionTreeElement: ActionTreeElement): ActionTreeElementBE {
        return ActionTreeElementBE(
            actionTreeElement.id,
            actionTreeElement.action,
            actionTreeElement.parent?.id)
    }

    fun fromBE(actionTreeElementBE: ActionTreeElementBE): ActionTreeElement {
        TODO("currently not needed")
    }
}