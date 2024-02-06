public class PermissionsTest {

  /*  @Test
    public void hasPermissionTest() {
        Permission permission = Permission.of("testpermission.test");
        Permission superPermission = Permission.of("supertestpermission.test.super.*");
        User user = new UserImpl(new UserDTO());

        user.setPermission(permission, true);
        user.setPermission(superPermission, true);

        Assertions.assertTrue(user.hasPermission("testpermission.test"));
        Assertions.assertTrue(user.hasPermission("supertestpermission.test.super.*"));
        Assertions.assertTrue(user.hasPermission("supertestpermission.test.super.hui"));
        Assertions.assertFalse(user.hasPermission("supertestpermission.test.super"));
        Assertions.assertFalse(user.hasPermission("supertestpermission.test"));
        Assertions.assertFalse(user.hasPermission("testpermission"));
        Assertions.assertFalse(user.hasPermission("testpermission.test.test1"));

        user.setPermission(Permission.of("*"), true);

        Assertions.assertTrue(user.hasPermission("supertestpermission.test.super"));
        Assertions.assertTrue(user.hasPermission("supertestpermission.test"));
        Assertions.assertTrue(user.hasPermission("testpermission"));
        Assertions.assertTrue(user.hasPermission("testpermission.test.test1"));
        Assertions.assertTrue(user.hasPermission("aboba.aboba"));
    }*/
}
