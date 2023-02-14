package kz.app.asso.auth.service

import kz.app.asso.auth.model.Role
import kz.app.asso.auth.repository.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class RoleServiceImpl: RoleService {

    @Autowired
    lateinit var repository: RoleRepository

    override fun getList(): ArrayList<Role> {
        return repository.findAllByDeletedAtIsNull(Sort.by(Sort.Direction.DESC, "priorityNumber"))
    }
}
