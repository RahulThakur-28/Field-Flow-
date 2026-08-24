package com.rahul.fieldflow.data.workspace

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import javax.inject.Inject

class WorkspaceDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun getWorkspaceById(id: String): WorkspaceDto {
        return supabaseClient.postgrest["workspaces"]
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingle<WorkspaceDto>()
    }

    suspend fun findWorkspaceByCode(code: String): WorkspaceDto? {
        return supabaseClient.postgrest["workspaces"]
            .select {
                filter {
                    eq("company_id_display", code)
                }
            }
            .decodeSingleOrNull<WorkspaceDto>()
    }

    suspend fun getWorkspaceByOwnerId(ownerId: String): WorkspaceDto {
        return supabaseClient.postgrest["workspaces"]
            .select {
                filter {
                    eq("owner_id", ownerId)
                }
            }
            .decodeSingle<WorkspaceDto>()
    }
}
