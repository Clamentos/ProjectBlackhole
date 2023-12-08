package io.github.clamentos.blackhole.business.session;

// TODO: actual flags
public final record PermissionFlags(

    boolean flag1,
    boolean flag2,
    boolean flag3
    //...

) {

    public static PermissionFlags tagFlags(int crud) {

        //...
        return(null);
    }

    public long extractFlags() {

        long temp = 0;

        temp = temp | (flag1 ? 1 : 0);
        temp = temp | (flag2 ? 1 : 0) << 1;
        temp = temp | (flag3 ? 1 : 0) << 2;

        return(temp);
    }

    public long extractFlagsOthers() {

        return(0);
    }
}
