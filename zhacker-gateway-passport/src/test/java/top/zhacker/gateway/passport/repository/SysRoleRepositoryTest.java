package top.zhacker.gateway.passport.repository;

import top.zhacker.gateway.passport.domain.SysMenu;
import top.zhacker.gateway.passport.domain.SysRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * Created on 2018/2/7.
 *
 * @author zlf
 * @since 1.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SysRoleRepositoryTest {

    @Autowired
    private SysRoleRepository sysRoleRepository;

    @Autowired
    private SysMenuRepository menuRepository;

    @Test
    public void findOneTest() throws Exception {
        SysRole role = sysRoleRepository.findOne("2619a672e53811e7b983201a068c6482");
        log.info("【SysRole】 role={}", role);
    }

    @Test
    public void findMenuByRole() throws Exception {
        SysRole role = sysRoleRepository.findOne("2619a672e53811e7b983201a068c6482");
        List<SysMenu> menus = role.getMenus();
        for (SysMenu sysMenu : menus) {
            log.info(sysMenu.toString());
        }
        log.info("【SysRole】 role={}", role);
    }
}