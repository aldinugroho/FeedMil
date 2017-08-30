
$(document).ready(function () {
    var state_conv = function (id) {
        var stat = "";
        switch (id) {
            case "1":
                stat = "on";
                break;
            case "2":
                stat = "full";
                break;
            default:
                stat = "off";
                break;
        }
        return stat;

    }
    var conveyor_view = function (jenis) {

    }
    setInterval(function () {
        //bagian conveyor
        var conveyors = ["jagung", "sbm_kasar", "sbm_halus", "mbm", "katul", "grit"];
        conveyors.forEach(function (jenis) {
            $.ajax({
                dataType: "json",
                url: "./json_all.php?GROUP=conveyor&NAME=" + jenis + "_conveyor",
                success: function (data) {
                    var status = data[0].conv_state;
                    status = state_conv(status);

                    if (jenis == "jagung") {
                        var src = "images_" + status + "/" + status + "_1_0.png";
                        var src_line = "images_" + status + "/" + status + "_2_0.png";
                        $("#" + jenis + "_line").attr("src", src_line);
                        $("#" + jenis + "_conveyor").attr("src", src);
                        src_line = "images_multi/" + status + "_" + jenis + ".png";
                        $("#jagung_sbm_kasar_line").attr("src", src_line);
                        src_line = "images_multi/" + status + "_" + jenis + "_crusher.png";
                        $("#line_top_crusher").attr("src", src_line);
                        $("#jagung_conv").val(status);
                        var sbm = $("#sbm_conv").val();
                        if (sbm != "on" && status == "off") {
                            src_line = "images_multi/off_top_crusher.png";
                            $("#line_top_crusher").attr("src", src_line);
                            src_line = "images_multi/off_line_jagung_sbm.png";
                            $("#jagung_sbm_kasar_line").attr("src", src_line);
                        }
                    } else if (jenis == "sbm_kasar") {
                        var src = "images_" + status + "/" + status + "_1_1.png";
                        $("#" + jenis + "_conveyor").attr("src", src);
                        src_line = "images_multi/" + status + "_" + jenis + ".png";
                        $("#jagung_sbm_kasar_line").attr("src", src_line);

                        src_line = "images_multi/" + status + "_" + jenis + "_crusher.png";
                        $("#line_top_crusher").attr("src", src_line);
                        //cek jika off semua
                        var jagung = $("#jagung_conv").val();
                        $("#sbm_conv").val(status);
                        if (jagung != "on" && status == "off") {
                            src_line = "images_multi/off_top_crusher.png";
                            $("#line_top_crusher").attr("src", src_line);
                            src_line = "images_multi/off_line_jagung_sbm.png";
                            $("#jagung_sbm_kasar_line").attr("src", src_line);
                        }
                    } else if (jenis == "sbm_halus") {
                        var src = "images_" + status + "/" + status + "_1_2.png";
                        $("#" + jenis + "_conveyor").attr("src", src);
                        //line 
                        var src_line = "images_" + status + "/" + status + "_2_2.png";
                        $("#" + jenis + "_line_1").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_3_2.png";
                        $("#" + jenis + "_line_2").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_4_2.png";
                        $("#" + jenis + "_line_3").attr("src", src_line);
                        $("#sbm_halus").val(status);
                    } else if (jenis == "mbm") {
                        var src = "images_" + status + "/" + status + "_1_3.png";
                        $("#" + jenis + "_conveyor").attr("src", src);
                        //line 
                        var src_line = "images_" + status + "/" + status + "_2_3.png";
                        $("#" + jenis + "_line_1").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_3_3.png";
                        $("#" + jenis + "_line_2").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_4_3.png";
                        $("#" + jenis + "_line_3").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_5_3.png";
                        $("#" + jenis + "_line_4").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_6_3.png";
                        $("#" + jenis + "_line_5").attr("src", src_line);
                    } else if (jenis == "katul") {
                        var src = "images_" + status + "/" + status + "_1_4.png";
                        $("#" + jenis + "_conveyor").attr("src", src);
                        //line 
                        var src_line = "images_" + status + "/" + status + "_2_4.png";
                        $("#" + jenis + "_line_1").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_3_4.png";
                        $("#" + jenis + "_line_2").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_4_4.png";
                        $("#" + jenis + "_line_3").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_5_4.png";
                        $("#" + jenis + "_line_4").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_6_4.png";
                        $("#" + jenis + "_line_5").attr("src", src_line);
                    } else if (jenis == "grit") {
                        var src = "images_" + status + "/" + status + "_1_5.png";
                        $("#" + jenis + "_conveyor").attr("src", src);
                        //line 
                        var src_line = "images_" + status + "/" + status + "_2_5.png";
                        $("#" + jenis + "_line_1").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_3_5.png";
                        $("#" + jenis + "_line_2").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_4_5.png";
                        $("#" + jenis + "_line_3").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_5_5.png";
                        $("#" + jenis + "_line_4").attr("src", src_line);
                        var src_line = "images_" + status + "/" + status + "_6_5.png";
                        $("#" + jenis + "_line_5").attr("src", src_line);
                    }

                }
            });

        });
        //khusus crusher
        $.ajax({
            dataType: "json",
            url: "./json_all.php?GROUP=crusher&NAME=crusher_crusher",
            success: function (data) {
                var route = data[0].crusher_route;
                var state = data[0].crusher_state;
                if (state == "1") {
                    $("#crusher_aktif").css("display", "block");
                } else {
                    $("#crusher_aktif").css("display", "none");
                }
                if (route == "0") {
                    var src = "images_multi/" + "off_bottom_crusher.png";
                    $("#bottom_crusher").attr("src", src);
                } else if (route == "1") {
                    var src = "images_multi/" + "on_jagung_1_bottom_crusher.png";
                    $("#bottom_crusher").attr("src", src);

                } else if (route == "2") {
                    var src = "images_multi/" + "on_jagung_2_bottom_crusher.png";
                    $("#bottom_crusher").attr("src", src);
                } else if (route == "3") {
                    var sbm_halus = $("#sbm_halus").val();
                    if (sbm_halus == "") {
                        sbm_halus = "off";
                    }
                    var src = "images_multi/" + "on_crusher_" + sbm_halus + "_sbm.png";
                    $("#bottom_3_crusher").attr("src", src);
                    var src = "images_multi/" + "on_crusher_" + sbm_halus + "_sbm_1.png";
                    $("#bottom_3_crusher_1").attr("src", src);
                    var src = "images_on/on_5_1.png";
                    $("#bottom_3").attr("src", src);
                }
                if (route != "3") {
                    var sbm_halus = $("#sbm_halus").val();
                    if (sbm_halus == "") {
                        sbm_halus = "off";
                    }
                    var src = "images_multi/" + "off_crusher_" + sbm_halus + "_sbm.png";
                    $("#bottom_3_crusher").attr("src", src);
                    var src = "images_multi/" + "off_crusher_" + sbm_halus + "_sbm_1.png";
                    $("#bottom_3_crusher_1").attr("src", src);
                    var src = "images_off/off_5_1.png";
                    $("#bottom_3").attr("src", src);
                }
            }
        });
        //khusus buffer
        var buffers = ["jagung_1", "jagung_2", "sbm_1", "sbm_2", "mbm", "katul", "grit"];
        buffers.forEach(function (buffer) {
            $.ajax({
                dataType: "json",
                url: "./json_all.php?GROUP=buffer&NAME=buffer_"+buffer,
                success: function (data) {
                    if(buffer == "jagung_1"){
                        var status = data[0].fill_level;
                        $("#jagung_1_buff_stat").val(status);
                    }
                    if(buffer == "jagung_2"){
                        var status_jagung = $("#jagung_1_buff_stat").val();
                        var status_jagung_1 = data[0].fill_level;
                        var src = "images_multi/buffer_jagung/buffer_jagung_"+status_jagung+"_jagung1_"+status_jagung_1+".png";
                        $("#buffer_jagung").attr("src",src);
                    }
                    if(buffer == "sbm_1"){
                        var status = data[0].fill_level;
                        $("#sbm_1_buff_stat").val(status);
                    }
                    if(buffer == "sbm_2"){
                        var status_sbm = $("#sbm_1_buff_stat").val();
                        var status_sbm_1 = data[0].fill_level;
                        var src = "images_multi/buffer_sbm/sbm_"+status_sbm+"_sbm1_"+status_sbm_1+".png";
                        $("#buffer_sbm").attr("src",src);
                    }
                    if(buffer == "mbm" || buffer == "katul" || buffer == "grit"){
                        var status = data[0].fill_level;
                        var src = "images_multi/buffer_"+buffer+"/"+buffer+"_"+status+".png";
                        $("#buffer_"+buffer).attr("src",src);
                    }
                }
            });
        });

    }, 10000);
});

