package jp.glory.practice.agentic.auth.command.domain.model

import jp.glory.practice.agentic.libraryuser.command.domain.model.Email
import jp.glory.practice.agentic.libraryuser.command.domain.model.LibraryUserId

data class AuthAccount(
    val libraryUserId: LibraryUserId,
    val email: Email,
)
