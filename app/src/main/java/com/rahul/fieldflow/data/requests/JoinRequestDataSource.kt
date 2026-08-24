package com.rahul.fieldflow.data.requests

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import javax.inject.Inject

class JoinRequestDataSource @Inject constructor(
    private val supabaseClient: SupabaseClient
) {
    suspend fun submitRequest(employeeId: String, workspaceId: String) {
        supabaseClient.postgrest["join_requests"].insert(
            buildJsonObject {
                put("employee_id", employeeId)
                put("workspace_id", workspaceId)
                put("status", "pending")
            }
        )
    }

    suspend fun getMyRequests(employeeId: String): List<JoinRequestDto> {
        return supabaseClient.postgrest["join_requests"]
            .select(Columns.raw("*, workspaces(name)")) {
                filter {
                    eq("employee_id", employeeId)
                }
            }
            .decodeList<JoinRequestDto>()
    }

    suspend fun getPendingRequests(workspaceId: String): List<JoinRequestDto> {
        return supabaseClient.postgrest["join_requests"]
            .select(Columns.raw("*, profiles!join_requests_employee_id_fkey(full_name, email, phone, role)")) {
                filter {
                    eq("workspace_id", workspaceId)
                    eq("status", "pending")
                }
            }
            .decodeList<JoinRequestDto>()
    }

    suspend fun respondToRequest(requestId: String, action: String) {
        supabaseClient.postgrest.rpc(
            function = "respond_to_join_request",
            parameters = buildJsonObject {
                put("p_request_id", requestId)
                put("p_action", action)
            }
        )
    }
}
