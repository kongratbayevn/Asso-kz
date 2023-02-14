package kz.app.asso.auth.service

import kz.app.asso.auth.model.Role

interface RoleService {

    fun getList(): ArrayList<Role>

}
