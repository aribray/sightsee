package com.example.sightsee.model;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aryo on 28/1/16.
 */
public class RoutingHelper {

    public static ArrayList<LatLng> parse(String json){

        ArrayList<LatLng> routes = new ArrayList<>();
        JSONArray jRoutes = null;

        try {
            JSONObject jObject = new JSONObject(json);
            jRoutes = jObject.getJSONArray("routes");

            for (int i = 0; i < jRoutes.length(); i++) {

                JSONObject route = (JSONObject) jRoutes.get(i);

                JSONArray legs = route.getJSONArray("legs");

                for(int j = 0; j < legs.length(); j++) {
                    JSONObject leg = (JSONObject) legs.get(j);

                    JSONArray steps = leg.getJSONArray("steps");
                    for(int k = 0; k < steps.length(); k++) {
                        JSONObject step = (JSONObject) steps.get(k);

                        JSONObject polyline = step.getJSONObject("polyline");
                        String points = polyline.getString("points");
                        List<LatLng> latLngList = decodePoly(points);

                        routes.addAll(latLngList);

                    }

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }


        return routes;
    }

    public static ArrayList<LatLng> decodePoly(String encoded) {

        ArrayList<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

    public static String getDummyJson() {
        return "{\n" +
                "   \"geocoded_waypoints\" : [\n" +
                "      {\n" +
                "         \"geocoder_status\" : \"OK\",\n" +
                "         \"place_id\" : \"ChIJ2Udvjiwo1i0RJQH4rlheHBc\",\n" +
                "         \"types\" : [ \"street_address\" ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"geocoder_status\" : \"OK\",\n" +
                "         \"place_id\" : \"EkhKbC4gVGFwYWsgU2lyaW5nIE5vLjIsIFRhbWJha3NhcmksIEtvdGEgU0JZLCBKYXdhIFRpbXVyIDYwMTMxLCBJbmRvbmVzaWE\",\n" +
                "         \"types\" : [ \"street_address\" ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"routes\" : [\n" +
                "      {\n" +
                "         \"bounds\" : {\n" +
                "            \"northeast\" : {\n" +
                "               \"lat\" : -7.261516299999998,\n" +
                "               \"lng\" : 112.7536194\n" +
                "            },\n" +
                "            \"southwest\" : {\n" +
                "               \"lat\" : -7.9742734,\n" +
                "               \"lng\" : 112.6267683\n" +
                "            }\n" +
                "         },\n" +
                "         \"copyrights\" : \"Map data ©2016 Google\",\n" +
                "         \"legs\" : [\n" +
                "            {\n" +
                "               \"distance\" : {\n" +
                "                  \"text\" : \"96.8 km\",\n" +
                "                  \"value\" : 96816\n" +
                "               },\n" +
                "               \"duration\" : {\n" +
                "                  \"text\" : \"2 hours 17 mins\",\n" +
                "                  \"value\" : 8197\n" +
                "               },\n" +
                "               \"end_address\" : \"Jl. Tapak Siring No.2, Tambaksari, Kota SBY, Jawa Timur 60131, Indonesia\",\n" +
                "               \"end_location\" : {\n" +
                "                  \"lat\" : -7.26271,\n" +
                "                  \"lng\" : 112.7535341\n" +
                "               },\n" +
                "               \"start_address\" : \"Jl. Raung No.14, Klojen, Kota Malang, Jawa Timur 65119, Indonesia\",\n" +
                "               \"start_location\" : {\n" +
                "                  \"lat\" : -7.9636998,\n" +
                "                  \"lng\" : 112.6267683\n" +
                "               },\n" +
                "               \"steps\" : [\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.1 km\",\n" +
                "                        \"value\" : 143\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 29\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.964814899999999,\n" +
                "                        \"lng\" : 112.6274162\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Head \\u003cb\\u003esoutheast\\u003c/b\\u003e on \\u003cb\\u003eJl. Raung\\u003c/b\\u003e toward \\u003cb\\u003eJl. T.G.P.\\u003c/b\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"blro@illnTr@c@FC^SbAg@|@]\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9636998,\n" +
                "                        \"lng\" : 112.6267683\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"38 m\",\n" +
                "                        \"value\" : 38\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 9\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.9646891,\n" +
                "                        \"lng\" : 112.6277377\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Panggung\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"`sro@kplnTW_A\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.964814899999999,\n" +
                "                        \"lng\" : 112.6274162\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.2 km\",\n" +
                "                        \"value\" : 1156\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 184\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.9743036,\n" +
                "                        \"lng\" : 112.6298174\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eJl. Brigjend Slamet Riadi\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by SMA Muhammadiyah 1 Terakreditasi A Malang (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"hrro@krlnTp@[bB{@DAdBu@|@c@h@SVGTGB?JClAAl@?FAvAIhBMPA^Al@?D@v@J|@Pp@Jn@FpBLn@FF?@@H?`BF@?x@Bt@B|@?Z?n@A@?\\\\Gh@WlAu@BCPOLM@ABCp@k@v@y@TY\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9646891,\n" +
                "                        \"lng\" : 112.6277377\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"5.0 km\",\n" +
                "                        \"value\" : 4951\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"11 mins\",\n" +
                "                        \"value\" : 685\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.9332894,\n" +
                "                        \"lng\" : 112.6465437\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e after Avia (on the left)\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eContinue to follow Jalan Raya Malang - Surabaya/Jl. Gempol-Malang\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Indomaret (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"jnto@k_mnTEAMG{BiAECIE}BkAECeBy@mAi@UIsBu@mBu@MEuAc@A?oE{AYIwCeAiAc@w@]ICq@Yi@UmCsAk@[y@a@gAi@_@UkAk@SIUMUK[KmBw@kHsCe@QkAa@qC}@_DiAsBg@m@Os@QaBc@cB]uAWiAQg@IsB[w@Kc@IG?iB[GAc@GaAOo@Ii@IgAMwAQYEMAi@IqAOSEs@Io@Iq@IMAq@IIAe@I{Dg@[Go@Ic@IIAgCW_@E{@K[EiAOeBW}Ba@uAU]GKCgB[_Cg@wCq@gKeDkE{AQGSGgAa@YKcAa@SGYKICu@Wk@S_A]WKoAa@iAc@_@O[Ku@[wBaAq@[g@Ua@U{@a@MEkB}@cAc@{CwA_Ac@EC{@a@]QSIoB{@k@W\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9743036,\n" +
                "                        \"lng\" : 112.6298174\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"35.0 km\",\n" +
                "                        \"value\" : 34967\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"57 mins\",\n" +
                "                        \"value\" : 3398\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.665548299999999,\n" +
                "                        \"lng\" : 112.6996272\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Slight \\u003cb\\u003eright\\u003c/b\\u003e at Wahana Mobil onto \\u003cb\\u003eJalan Raya Malang - Surabaya\\u003c/b\\u003e/\\u003cb\\u003eJl. A. Yani\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eContinue to follow Jalan Raya Malang - Surabaya\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Rumah Bersalin Muhammadiyah (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-slight-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"`nlo@{gpnTc@a@iD}AOGOIEAoAm@IEKE}BgAQISIQKgGqCKEKEIEOGoIyD]Cw@_@MGGEa@QgD{AwAm@_@Ss@WA?e@MSGaCq@w@ScBe@cBg@y@Y{Ac@OE[IwFaB[KIE_Cy@[ISEICo@SGAs@WkA]KEIC_A[QGcA_@QG}By@{@W}Ai@k@UIEyAe@_A[QGIE[OaBi@e@Q[KcBk@gAa@WIMEm@Sk@SaBo@QKMCoAk@wB_AQIaAa@GCSIqAi@SKKEkBy@GCu@[o@YGEs@YEC_@OYMEC[MEAWKMIEAWMe@UWKe@UOIgD{AWKGE}As@i@YaAe@OIk@WSIUK[M[Oa@Qe@Q{Ao@WMk@WeAc@AAYMc@SMEkAk@g@Uo@YQGSKkAg@e@UKE_Ae@y@]u@[eAc@cAe@}Bw@ME_Bs@eBu@k@U]MgAa@y@[mBw@IEMEqDsAeDqAaAa@{@_@[Oy@a@wAq@_@O[QKEqEgCCA{@]cCsAgBaA_B_Ao@_@cCqAyBkAAAe@YYOKEmBcAq@_@s@a@s@][QkAm@kAo@UMkB_AYQkB_Au@a@WOIGOI_@SWMy@e@m@]GCk@]yBiAOIGCu@c@MG{@g@WM}@a@u@a@cAk@OGmBaAgB{@aAi@aB{@QIc@WCAmEoB]MyAo@o@YaDgAu@WoC_AwDoAw@Um@QsEyACA}EeB{EyBoGiDgB_AYOOIoBqAc@YWQEEiE{CQOECu@i@q@g@GGA?{CaCOMkA}@uCyBq@g@yC}BKKw@s@gB{ASOa@_@oAeAeC_CACcAy@iBaBo@k@}@w@QQy@s@gAeAyAqAgBwAcB}AwBkBGGMI}BiB[Ym@i@k@e@o@e@SOyAgAy@g@YOOGCAk@YECyDcBaAa@a@S{@_@oBy@iAc@}Ao@wB_Ac@Q[MeDkAmCq@g@MiEeA}Be@iC[aBUs@GSC}@E_BE_AAWAy@AwAC[@U@W?wAJiBH{@Fw@HeAJo@Hk@H}BRs@HoCVI@]Bc@@{@BG?g@BG?K?cADaCHgCHmDD{AByHCk@Ai@Em@KMCIAUGOEOEKEkAi@eAm@iAo@iAs@_@UaBoAcDeCwC{BcAs@QSkA}@g@c@WSQOoAcA_@YUUSUQUACKSACMUEMIWESCQAW?C?U@EBYNwA?Q?QCOAIESM]KUIOa@o@o@o@uAmAyAmAq@q@WYSUa@q@Q_@c@{@a@y@sCcGkD_HiBqDi@cAiEcI]m@GKcHaMKSU_@iBwCaBoC]k@Wi@gCkEIOYa@c@q@]g@GI[i@}@yAq@gAa@o@uA_CqA_CQ[Qc@}@yBYo@Ys@mAkCq@yAYo@aBkDIQKUs@}AqAoC]o@[k@IK[e@}@oAMQIKk@y@{AmBCE_BmBW_@GI{@gA_FyF}@aASU_B}AiAeAq@m@u@m@q@k@_BoAUQiAu@i@_@wAy@i@[m@[}BqAu@_@wAs@oBy@ECwBu@yB{@iAe@y@[w@YkBq@c@OOGgAc@oAg@yAm@_CaAqB{@mA]QGsBg@o@M{@OYEyAUiAMQAkBQsE]o@C}AMqAKYCa@EGAk@EoBSOAaAK{Fo@[Cy@IsAQkBUSEcASA?UE]GcB]}A_@WIoBg@aBc@SGoA]o@Sa@M[IyDgAkCw@YKk@Qw@WqBq@mEsA}@WaAYcAYWGc@Mm@Qe@MYKSG_@Oc@SoAs@KGiBkAw@g@s@e@gCqAGCg@U_@SKEICMCKAOAK@I@KBODIDSNSLc@d@y@v@kBjBkAjAu@r@gAdAQR]ZuC~Bi@l@UTqBpBw@v@s@t@QRqA~AgAdAa@b@CBMLy@v@EFKJYXCBe@`@GF}BxBMLONqAdAi@`@w@j@}@p@QLcBfAy@h@c@XyAbA}GvEsBxA}AfAkBrAYPGDsAn@MFe@TcGdB}Br@yBn@iEnASFo@PiBf@SF{@TQD}A^iAXmAZ_FnAi@LMD_B`@_ATiBd@qA\\\\qA\\\\C?sAZcE|@}@RQDa@JmBd@m@NgCn@WHgAV_@J[H_@JiAVE@wFvAWFqCj@SFi@LaAV}GzAaCf@iDv@eAVaB\\\\qCl@qAXcDt@o@Lq@NeDr@s@NiDt@{A\\\\g@Lo@Nw@PaEt@sCt@SFYJcA`@m@V_A`@wBbAKDcAd@q@Ze@ToBbASJgAj@kAl@WLeAh@c@Tm@Zq@\\\\yAt@gAd@m@TWHsBr@kBl@KDeDjA_@LYLGBKBQHyBlAYNEBu@h@kBrAgDbCMJQLc@ZeD`CaCrAaA`@WJSHGBk@NUFWDA?_@J]Fa@HOBg@JA@yB\\\\m@JiBZi@HSBcDh@oAPiC^GByB^s@Na@JaB^}Bl@qCp@cDv@yBj@eE`A}GbB}A`@uCr@qA\\\\s@PcAPQDq@JMBqBXwATwATaBTO@aCZkDd@e@FoEl@cEn@uEz@_El@oB^s@JOBS@m@DcAHiDPU@sBFqERkABgADwCJ_BNUDyAVWFiAXKBgBj@oAb@e@Pm@TQHq@V}Al@q@VoC`AmDjAgAVC@aB`@}Bf@oBb@{A^}Cp@IBe@LeA\\\\c@TaAh@sBfAw@^k@Xq@VeAZqA\\\\sBd@aI|A{B`@}@LgBZqDz@]HWH[LC@m@VyAn@}@`@MF_@No@ZGDc@Ri@XuAr@KDqAp@A@WLaBv@wBdASHa@RcA\\\\]J_AXs@T\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9332894,\n" +
                "                        \"lng\" : 112.6465437\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.0 km\",\n" +
                "                        \"value\" : 977\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 115\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.6638782,\n" +
                "                        \"lng\" : 112.7071926\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePartial toll road\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"tdxm@usznT_@L@Q@q@@g@JuBRqEHcCBkDBkDEuACqAGaAEk@KwACOAOEa@EQGMEKIOWa@_@[UMWMMEo@M_@CsAG\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.665548299999999,\n" +
                "                        \"lng\" : 112.6996272\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"12.8 km\",\n" +
                "                        \"value\" : 12781\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"8 mins\",\n" +
                "                        \"value\" : 509\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.556712,\n" +
                "                        \"lng\" : 112.7085276\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Tol Gempol - Pandaan\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eToll road\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"fzwm@}b|nT}If@oIj@sEPyGBqFMuLc@uEOiFEwGHqDRiEb@yJfAuFb@{DLwDD}CAuDKaEUqFk@cEm@{Dm@gFu@uEe@aE[eEQ_EIoE?gDDeHZeF`@mCVgFv@eEr@{Dr@kDl@wAJgBDsBDuAG{@IyAQkCY_D]mFk@qD_@}Dc@gEe@uEe@wEg@aFg@oDe@eFu@mE}@eFmAyHsBeIuBuEkAqDw@eEs@wCi@yGkA_KcB_KcBuJcBqHoA}GmAoDi@{Dg@iHg@eFS{EGcFDuCJeBFeABeI`@gH\\\\wI\\\\kFH_E?_AC{BGqBKiCMqEa@QAyDc@gG_A_Cg@}@S_ASyFmAkCk@uFiAwDu@aBUqASqAQuBOqBCsBNcCV}@NiBd@uAh@cCjAmAv@]Vm@h@_B|AgBbCqBvEm@`Ca@nCMxCBfEPpDLhB\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.6638782,\n" +
                "                        \"lng\" : 112.7071926\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"3.9 km\",\n" +
                "                        \"value\" : 3879\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"5 mins\",\n" +
                "                        \"value\" : 287\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.5399858,\n" +
                "                        \"lng\" : 112.6831866\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Tol Porong - Gempol\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eToll road\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"l|bm@ik|nTDv@Dt@F~@@H@XBXFpA@PD~@Dz@Dd@HvAFfB?d@?j@CbAGdAIfAMrAQlAQbAOr@Qt@]rAeAnDaBtFaBrFSn@Wz@M`@{BxHu@dCGTIRGRq@|B_@nA_@rAK\\\\ELOb@Ql@Gr@Kr@M|@Op@Ol@CL]bA?DUv@EPMp@Kj@AHEVGh@CVCXCf@c@xAs@zB_@dA_@|@c@z@_@t@MPKPCBILeA`BIJMNORKLc@f@c@h@[ZC@?@C@WVSP{AlAuA`AcAn@u@`@kAl@}@Z_Bn@k@NMDE@C@C@KBuA\\\\mAPcC^gBZa@DoBRq@?iABM?oA?i@?uBC}ASuA_@YGeB]UG\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.556712,\n" +
                "                        \"lng\" : 112.7085276\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.7 km\",\n" +
                "                        \"value\" : 662\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 63\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.534724799999998,\n" +
                "                        \"lng\" : 112.6859627\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Arteri Baru Porong\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eToll road\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"|s_m@}lwnTmB]wBq@k@W]OECqAq@k@W[MeB{@}BkAoAm@qAq@c@S}As@MGw@]\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.5399858,\n" +
                "                        \"lng\" : 112.6831866\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.4 km\",\n" +
                "                        \"value\" : 1380\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 104\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.523909399999999,\n" +
                "                        \"lng\" : 112.6920917\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Keep \\u003cb\\u003eright\\u003c/b\\u003e to stay on \\u003cb\\u003eJl. Arteri Baru Porong\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eToll road\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"keep-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"~r~l@g~wnTqAq@a@Uu@a@iB{@eBw@cB{@_CoAmDeBcCoA{B_A{By@oBs@yBgAkQkIyAu@yAq@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.534724799999998,\n" +
                "                        \"lng\" : 112.6859627\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.3 km\",\n" +
                "                        \"value\" : 1305\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 97\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.5136613,\n" +
                "                        \"lng\" : 112.6976903\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue straight to stay on \\u003cb\\u003eJl. Arteri Baru Porong\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eToll road\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"straight\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"lo|l@qdynTuOyHsDiBuCyA}BsAuDaC}DyBMI{@a@KGYMwAu@gAc@_A[mA]qBc@}ASaAC\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.523909399999999,\n" +
                "                        \"lng\" : 112.6920917\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.5 km\",\n" +
                "                        \"value\" : 548\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 43\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.509181,\n" +
                "                        \"lng\" : 112.6992877\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Take the ramp to \\u003cb\\u003eSurabaya\\u003c/b\\u003e/\\u003cb\\u003eLewatJalanTol\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eToll road\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"jozl@qgznTQBU?cAAC?kBAE?I?M?w@Au@Eu@IWIa@KSIm@Yi@]OKIGc@[uAcAo@c@_@Sc@QQEa@Q\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.5136613,\n" +
                "                        \"lng\" : 112.6976903\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"23.9 km\",\n" +
                "                        \"value\" : 23870\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"22 mins\",\n" +
                "                        \"value\" : 1331\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.308614,\n" +
                "                        \"lng\" : 112.7077667\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Merge onto \\u003cb\\u003eJalur Pantura\\u003c/b\\u003e/\\u003cb\\u003eJl. Tol Surabaya - Gempol\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eToll road\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"merge\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"jsyl@qqznTeEM{@Am@@gA@oAFcAFy@Hw@Le@FeBZy@P{Ab@aAZIBWHIDUHoAh@gAd@u@^g@XmBbAc@VyFbDqMxHs@`@KFkEdCaIpEyNhI}CfByC~AmCpAi@TgAb@uAj@uAf@sA`@sA`@o@PcAVuAXoDp@{ATgBTgCXwBNgAFyBJgBDuA@}B?{DEuBI_BKcCSmCWwCk@{AUcASoDu@}ImBWGQE[IgAUuBc@sIgBkCk@gAUwBe@_B_@qAYmDy@GCA?ICMCEACA[Gs@Sk@MIC]IeBc@wCs@aAUa@KcBa@gAYoCs@gCk@y@QcCm@yA[_Ci@cE}@sLeCiDs@mIeBcDq@wBe@gE}@_BYyAWo@KgDg@kDa@cCU{Ge@iAG}@CU?E?[AmBE{@?i@B_ADc@@kA?sB?aCByDHiDL{@F_BJwANK?o@H}CZuDd@I@MBG@QBQDG@SDeF`AkAX_B\\\\eBb@eCr@cD~@}GvBqEtA{A`@]HcDz@qBd@{@PeAPw@LcALcAJ}@FyAHaADg@Bo@@gA@uA@kAC_AAS?yBKq@GqAM]CSC}AQs@KaAMCAQCcB[w@Os@MmDs@cCg@o@Mi@KyBa@{B_@eBWo@IkBUaBO_BOo@Eg@EaBKqAIqBIiBGoBEi@?UAaCAgD@]?O@kBDyCHkG^}ALsBPs@HmBPoBPeFb@gADoBJkBFy@@{EJ{BAiB?uDG{AEaACkAGuAIi@C}@GcAKSAGAwBSgAKcLuAgH{@mJmAiEc@aEg@mBSw@IqCWmNmA{@GG?g@EcSuAWC]EeF_@_Hw@}ASoGgA{Dy@_Dy@gCw@yGsBeCcA{@a@YMyDcBSKEA{@c@qAq@gAm@o@]mAu@_BcAcAo@wA}@uE_DCAaAm@q@a@kCcB}A}@iHwDeBw@oB}@_C_A}Am@eAa@yAe@cHyB_@KmHiBkBc@A?cHmAo@IOCiHaAeHy@_@Ee@GSCKAEAWEiAMSCKCa@EaC[s@IyAMy@@_AGwASm@OoCo@kAWyDq@A?QE[GOESEaAQo@Oa@Ia@IKCsCo@aEaAi@MmCo@GAwEcAwDs@mB[oB[wCe@aBS_BSqBU_BQyAOcAIiAKg@EYAy@GoAKs@Gk@EqCS}BQiCUuCUsBS_AIuGu@mB[uBWqBWmEk@iAQc@Io@Iq@Kk@I_AM_AO_AKg@Ie@IEAi@Ic@Gq@I[Gg@Gg@Ia@Gc@Ga@GYC[Ea@Ig@Ga@Ga@Ee@EYCEASCc@C}BMa@?]?Y?a@?e@@[@U@S@O@M@g@Hc@D[Dc@JaAVe@Nk@Tc@PYLYL]P_@TWNg@Xc@ZYPYR]VYRMJURiJbIe@d@_ClCILA@KLUZ_@h@U\\\\U`@Yh@S^Q`@IRCFA@CHCD?@EHKTQd@Sn@Of@Sp@Qt@Mh@G\\\\ADAD]fCs@tD\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.509181,\n" +
                "                        \"lng\" : 112.6992877\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.6 km\",\n" +
                "                        \"value\" : 627\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 57\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.3102664,\n" +
                "                        \"lng\" : 112.7104849\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Take exit \\u003cb\\u003e12\\u003c/b\\u003e toward \\u003cb\\u003eGunung Sari\\u003c/b\\u003e/\\u003cb\\u003eWonokromo\\u003c/b\\u003e/\\u003cb\\u003eKarang Pilang\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eToll road\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"ramp-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"xmrk@qf|nT?ZCZCT?L?L?H?JBJ@H@FBFFLBFDFFFFFHDLDB@^FVCHAHEJKHIHKDIDIBMDOBS@U@KBc@Bi@Bk@TmEJcALk@BMZiAVeAJ[\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.308614,\n" +
                "                        \"lng\" : 112.7077667\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"94 m\",\n" +
                "                        \"value\" : 94\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 16\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.309738599999999,\n" +
                "                        \"lng\" : 112.7110582\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Raya Mastrip\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePartial toll road\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"dxrk@ow|nT?M?G?EAC?CACAC?CAAMQsAo@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.3102664,\n" +
                "                        \"lng\" : 112.7104849\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"2.4 km\",\n" +
                "                        \"value\" : 2397\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"5 mins\",\n" +
                "                        \"value\" : 328\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.300757399999998,\n" +
                "                        \"lng\" : 112.7290322\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Gunung Sari\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Viele (on the left in 1.2&nbsp;km)\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"ztrk@c{|nTKEKGSK[Oi@]QM[_@]a@KSOQUSUWWYUWOQSUWWIEEEYUEESWOMEIGGGICIEIGKAGEMEQCMAIAMC]?_@Ae@?c@?]@K?G?G@E@EX}@Jo@Nu@Hg@B_@?[Cw@EwAKmDm@_JKaBEgAAQCmAEiAAOCk@C_@I_@?ASm@]gAo@cBWm@Yq@QY]g@Y]QUs@y@c@_@}@u@g@]g@[cAk@e@Yg@YUO_BaAeAe@cAm@ACEAWQIEIGCAk@c@OMCEKMS[Ug@k@sAWs@Yo@GMUk@a@{@OWKW\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.309738599999999,\n" +
                "                        \"lng\" : 112.7110582\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.0 km\",\n" +
                "                        \"value\" : 1032\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 116\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.298333400000001,\n" +
                "                        \"lng\" : 112.7377712\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Joyoboyo\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Langoan Jaya (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"v|pk@mk`oTiAcDAEISGOEGGKKQa@i@cBaCe@o@q@mAS_@EGOi@Ka@Ia@Ea@E]A[AUASE}@E_DAqD@UCoAEoAGaCG{BIg@Mm@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.300757399999998,\n" +
                "                        \"lng\" : 112.7290322\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.3 km\",\n" +
                "                        \"value\" : 266\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 38\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.296572200000001,\n" +
                "                        \"lng\" : 112.7391942\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Slight \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJalan Raya Malang - Surabaya\\u003c/b\\u003e/\\u003cb\\u003eJl. Raya Darmo\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-slight-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"pmpk@abboTOM[c@S[We@Ug@KQMQQSMKAAKIOIKGq@Qk@IO?A?WAE?\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.298333400000001,\n" +
                "                        \"lng\" : 112.7377712\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"2.2 km\",\n" +
                "                        \"value\" : 2161\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"5 mins\",\n" +
                "                        \"value\" : 313\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.277383599999999,\n" +
                "                        \"lng\" : 112.7412239\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Keep \\u003cb\\u003eright\\u003c/b\\u003e to stay on \\u003cb\\u003eJalan Raya Malang - Surabaya\\u003c/b\\u003e/\\u003cb\\u003eJl. Raya Darmo\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by PRO CLINIC (on the right)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"keep-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"pbpk@}jboTkAH{@?gAA[ASAYAcCFuCNa@ByBFaABe@?qBHcC?a@@}@DgABaA@y@CqEo@}C]_@EcBO]@UEuCa@OAkFg@mAMSC_@E[CiGo@cEe@_BQ[EqAM}AQwBU[EcEe@aGs@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.296572200000001,\n" +
                "                        \"lng\" : 112.7391942\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.5 km\",\n" +
                "                        \"value\" : 494\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 56\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.2730538,\n" +
                "                        \"lng\" : 112.7420504\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"At Kimia Farma, continue onto \\u003cb\\u003eJl. Urip Sumoharjo\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by UTAMA MOTOR (Battlax Indonesia) (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"rjlk@swboTMCcAQIAA?e@GGCqAIk@I_CWOCcAMQC]GSEUEo@KOCcB_@MAGAGAKAKAG?I?a@D[F\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.277383599999999,\n" +
                "                        \"lng\" : 112.7412239\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.1 km\",\n" +
                "                        \"value\" : 1127\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 172\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.263059999999999,\n" +
                "                        \"lng\" : 112.7408357\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Merge onto \\u003cb\\u003eJl. Basuki Rahmat\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Bumi Surabaya City Resort (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"merge\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"pokk@y|boTqAXKB{AV_AJ{BR[@A?Y@mABgA@{ABiBLO@S@e@Bc@D]Be@BoAJoGt@eCP[?UAWAa@Ee@AsCGqACU?WBG@k@LKB]HOBI@MD\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.2730538,\n" +
                "                        \"lng\" : 112.7420504\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.6 km\",\n" +
                "                        \"value\" : 555\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 71\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.264403499999999,\n" +
                "                        \"lng\" : 112.7454586\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e after KFC (on the left)\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Bank Danamon (on the right)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"bqik@guboTSKKGEECECGAEAGAMTu@Pk@FYVqAVy@XeA^wATy@rBmHZiA\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.263059999999999,\n" +
                "                        \"lng\" : 112.7408357\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.3 km\",\n" +
                "                        \"value\" : 260\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 37\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.262306599999999,\n" +
                "                        \"lng\" : 112.7462531\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Slight \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Yos Sudarso\\u003c/b\\u003e (signs for \\u003cb\\u003eBalai Kota\\u003c/b\\u003e/\\u003cb\\u003eT H R\\u003c/b\\u003e)\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Zangrandi (on the right)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-slight-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"nyik@crcoTCOAICGGKkF{@kB[w@W\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.264403499999999,\n" +
                "                        \"lng\" : 112.7454586\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.3 km\",\n" +
                "                        \"value\" : 325\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 41\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.2615884,\n" +
                "                        \"lng\" : 112.7489212\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Slight \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eJl. Ketabang Kali\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Hj. Boyatien (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-slight-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"llik@awcoTWMi@Yi@aBI]Me@C]Ec@Eo@I{ACSA_@?I@SBMBKHM\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.262306599999999,\n" +
                "                        \"lng\" : 112.7462531\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.2 km\",\n" +
                "                        \"value\" : 233\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 30\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.263053899999999,\n" +
                "                        \"lng\" : 112.7504077\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Merge onto \\u003cb\\u003eJl. Gubeng Pojok\\u003c/b\\u003e/\\u003cb\\u003eJl. Plaza Boulevard\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eContinue to follow Jl. Gubeng Pojok\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Convention Dan Exhibition (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"merge\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"|gik@wgdoT@C?AHMT]T[Zg@JINO~AqAPMn@i@PO\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.2615884,\n" +
                "                        \"lng\" : 112.7489212\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.4 km\",\n" +
                "                        \"value\" : 404\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 43\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.264305999999999,\n" +
                "                        \"lng\" : 112.7535743\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Keep \\u003cb\\u003eleft\\u003c/b\\u003e to continue on \\u003cb\\u003eJl. Prof. Dr. Musotopo\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"keep-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"`qik@aqdoTdCqALMJKHQ?ABC@EFKDKPs@Fe@DoADmAB_@?A?E?K@OBc@@k@Dm@D[\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.263053899999999,\n" +
                "                        \"lng\" : 112.7504077\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.2 km\",\n" +
                "                        \"value\" : 184\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 25\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.26271,\n" +
                "                        \"lng\" : 112.7535341\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Tapak Siring\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eDestination will be on the left\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"|xik@ydeoTQGIAE?E?C?E@E?GBEBEBGDEBC@C@C@E@U?MEoDG\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.264305999999999,\n" +
                "                        \"lng\" : 112.7535743\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  }\n" +
                "               ],\n" +
                "               \"via_waypoint\" : []\n" +
                "            }\n" +
                "         ],\n" +
                "         \"overview_polyline\" : {\n" +
                "            \"points\" : \"blro@illnT~CcB|@]W_AtCwArEoB|@SdL_@fKhAxHPxBa@pBwAnBkBN[iCqAsCyA}HcD_ToHyMmGaR}H{UiHu]mFiVaDwP{BgF}@kZ{IcGwBoKwDc^iP{YkNg[wMka@wLgo@_U{pA}j@qa@oPmb@aUmn@o\\\\cMoGu[wL}HcCyL_FwJiF}DeCyFcEoJkHcQkN{`@{]kMyJ{I}DmPaHePqE}K_BwLWyM~@uNjA_Qb@kNG{AYqJcF_SoOkEgEk@aCNkEi@wAaG{F_CsCkFyK_Zij@cM{SmI_NgJcSqImQgGmImQoSmMeKqPuImQwGqJ{DeIiC{RyBw`@}DcMqCyRuFc[uJiNaIuBa@wGvFoMxLuQbR_LjJwZ|ScCnAaP~EwPtEkf@rLqa@|Jsw@bQaLdCkEfBkN|GoMpGqOnFaJpFkFxDaKbGuD~@wKjBiR~Cqe@jLcPdDeYzDoYpE}UbAgJx@uEpAgOtFcYdHsLpF}HvBcRhDmMhEuU|KqDhAPaFd@mTWuGu@uEeByAqD_@mTrAmNTgb@gAiM\\\\cQjBqLp@uIBwJa@uLyA{XeD}VUkO|@uJnAeS`DmJEak@eGoYkDs`@uJeVaFok@wJ{\\\\oFoO{@_MAgSx@k`@dAwLe@eTgCsc@_JkKy@_L|AeIdEmCfCyEzIoApGI`Jj@hKd@|IVjGU|FaAxGwEnP_JjZmDrLmDhR_BvGcCtFuCfEoCxCiGpE_HzCiD|@{IrAyFVmIWkFmAoHwB_D{AmQuIkWoMkN}FyZ{N}]qQ}LgHeEoB}IqBmHCqDQ{By@qH{EuHg@iGR_NpC{KfFwx@`e@oNnHgIxC}GdB{NvBaLh@eOMiOcBmTmEkX{FyNeDs]qIgo@cNa`@}GmUoAeUXmX|BeIzAgc@~LuRvD{O\\\\aN}@_f@iIiTsAsR?ob@zC}IZwR@_Ou@ms@mIuo@gF{PaBiUoEcRqGmF_CoQiKaZkQw[qL{KmCmS}CsNcBkMgAkQkDij@gLsW{C{PoA_a@kEiQeC_QcCiFi@qIG_G~@_GjC{EfDyOdOcAtA{BhEoAnDqCxNCjCh@fAz@Tv@Wn@qB`AqM|@oEsBuAqDqCyCkDqBoBo@yAMkEz@kEH{CkAgUe@mJcE}JcCmCqE{CuI_F{BgBoIcSsGmKm@gGi@mXuCmFgDyA{GBkQb@uIPiCDkGs@_Iq@gLqAgViCcd@iFaIuAiBCoL~A_M`@_S~A{JOeB^w@KOYb@wBvGuVQm@gL}BsA{BWcAYmEN}BlE}E|FgEr@kENuDFyAKc@YAq@XaFG\"\n" +
                "         },\n" +
                "         \"summary\" : \"Jalan Raya Malang - Surabaya\",\n" +
                "         \"warnings\" : [],\n" +
                "         \"waypoint_order\" : []\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}";
    }

    public static String getDummyJSONMalangBlitar() {
        return "{\n" +
                "   \"geocoded_waypoints\" : [\n" +
                "      {\n" +
                "         \"geocoder_status\" : \"OK\",\n" +
                "         \"place_id\" : \"ChIJ2Udvjiwo1i0RJQH4rlheHBc\",\n" +
                "         \"types\" : [ \"street_address\" ]\n" +
                "      },\n" +
                "      {\n" +
                "         \"geocoder_status\" : \"OK\",\n" +
                "         \"place_id\" : \"ChIJ387_H6XueC4RpR1hEaQLDfU\",\n" +
                "         \"types\" : [ \"administrative_area_level_4\", \"political\" ]\n" +
                "      }\n" +
                "   ],\n" +
                "   \"routes\" : [\n" +
                "      {\n" +
                "         \"bounds\" : {\n" +
                "            \"northeast\" : {\n" +
                "               \"lat\" : -7.9636998,\n" +
                "               \"lng\" : 112.6305065\n" +
                "            },\n" +
                "            \"southwest\" : {\n" +
                "               \"lat\" : -8.160255299999999,\n" +
                "               \"lng\" : 112.1300221\n" +
                "            }\n" +
                "         },\n" +
                "         \"copyrights\" : \"Map data ©2016 Google\",\n" +
                "         \"legs\" : [\n" +
                "            {\n" +
                "               \"distance\" : {\n" +
                "                  \"text\" : \"79.0 km\",\n" +
                "                  \"value\" : 78960\n" +
                "               },\n" +
                "               \"duration\" : {\n" +
                "                  \"text\" : \"2 hours 13 mins\",\n" +
                "                  \"value\" : 8009\n" +
                "               },\n" +
                "               \"end_address\" : \"Sanan Kulon, Sanankulon, Blitar, East Java, Indonesia\",\n" +
                "               \"end_location\" : {\n" +
                "                  \"lat\" : -8.094609999999999,\n" +
                "                  \"lng\" : 112.1300221\n" +
                "               },\n" +
                "               \"start_address\" : \"Jl. Raung No.14, Klojen, Kota Malang, Jawa Timur 65119, Indonesia\",\n" +
                "               \"start_location\" : {\n" +
                "                  \"lat\" : -7.9636998,\n" +
                "                  \"lng\" : 112.6267683\n" +
                "               },\n" +
                "               \"steps\" : [\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.1 km\",\n" +
                "                        \"value\" : 143\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 29\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.964814899999999,\n" +
                "                        \"lng\" : 112.6274162\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Head \\u003cb\\u003esoutheast\\u003c/b\\u003e on \\u003cb\\u003eJl. Raung\\u003c/b\\u003e toward \\u003cb\\u003eJl. T.G.P.\\u003c/b\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"blro@illnTr@c@FC^SbAg@|@]\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9636998,\n" +
                "                        \"lng\" : 112.6267683\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"38 m\",\n" +
                "                        \"value\" : 38\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 9\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.9646891,\n" +
                "                        \"lng\" : 112.6277377\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Panggung\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"`sro@kplnTW_A\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.964814899999999,\n" +
                "                        \"lng\" : 112.6274162\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.2 km\",\n" +
                "                        \"value\" : 1156\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 184\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.9743036,\n" +
                "                        \"lng\" : 112.6298174\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eJl. Brigjend Slamet Riadi\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by SMA Muhammadiyah 1 Terakreditasi A Malang (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"hrro@krlnTp@[bB{@DAdBu@|@c@h@SVGTGB?JClAAl@?FAvAIhBMPA^Al@?D@v@J|@Pp@Jn@FpBLn@FF?@@H?`BF@?x@Bt@B|@?Z?n@A@?\\\\Gh@WlAu@BCPOLM@ABCp@k@v@y@TY\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9646891,\n" +
                "                        \"lng\" : 112.6277377\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.9 km\",\n" +
                "                        \"value\" : 863\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 156\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.9815929,\n" +
                "                        \"lng\" : 112.6303891\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e after Avia (on the left)\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eContinue to follow Jl. Gempol-Malang/Jl. Jenderal Basuki Rahmat\\u003c/div\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by McDonald's (on the right)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"jnto@k_mnTJMJBVHl@Pb@J`@JH@`@Jb@FH@N@B?p@B^@D@p@@P@jBD|@?NAF?`AChAEj@Cf@@bAGh@G\\\\G\\\\K^SVUf@e@BCLO^_@JKJIBCDCDAFEHCB?DANCL?F?r@BR@B@nBT\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9743036,\n" +
                "                        \"lng\" : 112.6298174\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.2 km\",\n" +
                "                        \"value\" : 189\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 39\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.9832147,\n" +
                "                        \"lng\" : 112.6298822\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"At Sarinah Cineplex Cinema, continue onto \\u003cb\\u003eJl. Merdeka Barat\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by GPIB Jemaat \\\"Immanuel\\\" Malang (on the right)\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"|{uo@}bmnTXHRFhCf@H@|@RbAT\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9815929,\n" +
                "                        \"lng\" : 112.6303891\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.3 km\",\n" +
                "                        \"value\" : 335\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 53\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.9824698,\n" +
                "                        \"lng\" : 112.6269309\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"At Hotel Pelangi Malang, \\u003cb\\u003eJl. Merdeka Barat\\u003c/b\\u003e turns \\u003cb\\u003eright\\u003c/b\\u003e and becomes \\u003cb\\u003eJl. Kauman\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by SDN Kauman 1 (on the left)\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"`fvo@w_mnTGb@Y`BEV_@xBER[|BCREZ_@rB\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9832147,\n" +
                "                        \"lng\" : 112.6298822\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.2 km\",\n" +
                "                        \"value\" : 210\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 57\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.984083699999998,\n" +
                "                        \"lng\" : 112.6261072\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. K.H. Hasyim Ashari\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by UD. Brilliant (on the right)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"lavo@imlnTRDzBt@FBZJpCbACR\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.9824698,\n" +
                "                        \"lng\" : 112.6269309\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.5 km\",\n" +
                "                        \"value\" : 499\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 83\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -7.987269800000001,\n" +
                "                        \"lng\" : 112.6229779\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Arif Margono\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by CNI Malang (on the right)\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"nkvo@ehlnTX`@bAjBRZRTDFh@p@Z\\\\FH\\\\^vAdABBhBnAn@b@@@j@^t@h@JJLJPL\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.984083699999998,\n" +
                "                        \"lng\" : 112.6261072\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"4.1 km\",\n" +
                "                        \"value\" : 4138\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"9 mins\",\n" +
                "                        \"value\" : 566\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.0226717,\n" +
                "                        \"lng\" : 112.6179246\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"At Pemerintah Kota Malang Kel Sukun Kec Sukun, continue onto \\u003cb\\u003eJl. Sudanco Supriadi\\u003c/b\\u003e/\\u003cb\\u003eJl. Supriadi\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Kantorpos Malang Sukun (on the left in 2.8&nbsp;km)\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"l_wo@stknTZV@?ZVt@n@^\\\\DDPLPH\\\\HD@`@Jb@D`@@d@Fz@R|@Pd@LnAVbE|@TD|Cz@h@L|A^FBND@?RDB@tAZj@L^LJBLB@?fEbAB@bCj@^HB@B@NDF@vC~@~A`@v@P`ARz@Nx@PxBj@f@Ll@Nt@RzBj@bCh@ZJZFTBN@f@DN?P?`@A\\\\@L?L@nA?|AMlEc@^GxCk@j@MbBa@nDy@j@O~@UZIn@Qf@Kb@KtCy@b@MlB_@XE`@IREdBg@|@Mj@GVEt@ILCnAKhAKVCBAF?tBA\\\\@z@B`@@P?v@@r@@b@BlAFfAHR@bAFb@BfBHn@DN@dBLJ@lBPjARNBH@d@JXJz@TtAb@dD~@r@VFBnAh@VJRHHB|Af@n@TTHTJh@P~@XJD\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -7.987269800000001,\n" +
                "                        \"lng\" : 112.6229779\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"2.7 km\",\n" +
                "                        \"value\" : 2679\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"5 mins\",\n" +
                "                        \"value\" : 271\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.0445168,\n" +
                "                        \"lng\" : 112.6079098\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Raya Kb. Agung\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Koperasi Karyawan Sari Madu (on the right)\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"t|}o@_ujnTn@Rf@Pn@PVF`Bh@r@VbCt@^L`C|@tDlA\\\\JvBr@RDjDbA\\\\Jr@RVHTH|@ZJBVJfC|@hBl@`@J|Af@l@TnGzBbCt@ZLbEvAdBv@l@Z`@TjAp@r@`@t@b@d@XNH^PvHtEdBbAbAd@VJl@ZLDr@Zp@Xl@T\\\\Jv@Tn@LPDXF`Et@nB`@h@Hz@RnAXVFj@LhAP\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.0226717,\n" +
                "                        \"lng\" : 112.6179246\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.9 km\",\n" +
                "                        \"value\" : 1895\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 174\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.0602792,\n" +
                "                        \"lng\" : 112.6014656\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"At SDN Kebonagung II &amp; VI, continue onto \\u003cb\\u003eJl. Raya Genengan\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003ePass by Klinik Sehat Alamiah Rumah Herbal (on the right)\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"febp@mvhnTLBRFVBpATzDv@dARfCj@bB\\\\lDn@d@FjBd@dBb@pA`@zCdATHD@xCbAhC|@VHh@PdBl@dBj@l@RBBbEzAnCbAdA`@nAd@rAf@jCbA`Bl@r@VnAd@D@dAb@dC`A~@\\\\\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.0445168,\n" +
                "                        \"lng\" : 112.6079098\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.6 km\",\n" +
                "                        \"value\" : 1600\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 178\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.0729279,\n" +
                "                        \"lng\" : 112.594595\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Kolonel Slamet Supriyadi\\u003c/b\\u003e/\\u003cb\\u003eJl. Raya Pakisaji\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eContinue to follow Jl. Raya Pakisaji\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"vgep@engnTzB|@D@NFfC`AFBr@TrAj@bAd@lAd@PHRDzAn@TJ`Bn@d@P^RpCnAHBxAj@xBz@FBrAf@F@nAb@z@\\\\r@TFB|@\\\\RHt@\\\\`A`@fClAp@Zn@^B@fAn@|@f@~@h@DBB@`DnBNJvBpAVN\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.0602792,\n" +
                "                        \"lng\" : 112.6014656\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.7 km\",\n" +
                "                        \"value\" : 1664\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 151\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.086439,\n" +
                "                        \"lng\" : 112.588193\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Raya Karang Pandan\\u003c/b\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"xvgp@gcfnTp@b@pAx@hAp@v@b@r@b@f@Vj@VfA^z@Z~@ZxAf@tA\\\\|Bp@p@PjCr@~Cx@rDbAnA^lAb@x@ZvDrAvCbA~DxApAh@`C~@jBr@hD|Ah@V\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.0729279,\n" +
                "                        \"lng\" : 112.594595\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.2 km\",\n" +
                "                        \"value\" : 1205\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 124\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.096250299999999,\n" +
                "                        \"lng\" : 112.5835504\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Kolonel Slamet Supriyadi\\u003c/b\\u003e/\\u003cb\\u003eJl. Raya Pepen\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eContinue to follow Jl. Kolonel Slamet Supriyadi\\u003c/div\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"fkjp@e{dnT`DtAFBvCnAlBx@jBr@^N^Nf@P|CjAbA`@lDnAfA^hBp@~@\\\\FBLDnDlAXJ\\\\LnA`@lEvA~Ah@f@P\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.086439,\n" +
                "                        \"lng\" : 112.588193\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"4.4 km\",\n" +
                "                        \"value\" : 4388\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"5 mins\",\n" +
                "                        \"value\" : 286\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.116910599999999,\n" +
                "                        \"lng\" : 112.5551863\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eJl. Lkr. Bar. Kepanjen\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"phlp@e~cnTXJWxACJKn@QjAUxAIx@Ab@@RBTD^Lh@Rb@l@`A`@p@h@`A|@pAhAlBhAfBdA~AXh@d@v@DFR\\\\NTx@|AHPn@nALV~BxEn@pARd@Pb@HVFTHb@Lr@VxA@H\\\\tBDV^pB@DXvAFRDFDD`@Zp@f@ZP@?NJ@?^Nv@RdCl@b@Jj@NdAPbAJXBZH@?^JVJb@RrAz@bBrALHNLNPDFDHDJBJBH@JBL@LARALGVITQb@MX]bAqC~Ja@rAG^CVATBRDRDNNVz@fAbBrBzAfBTVxAtAbA|@`@XLHdBp@`A\\\\lGzBjHfCvHrCdGtBhC~@\\\\PRNNNf@l@fF~HlB`D\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.096250299999999,\n" +
                "                        \"lng\" : 112.5835504\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.3 km\",\n" +
                "                        \"value\" : 288\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 30\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1193566,\n" +
                "                        \"lng\" : 112.5543629\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Slight \\u003cb\\u003eleft\\u003c/b\\u003e toward \\u003cb\\u003eJl. Raya Gn. Kawi\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-slight-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"tipp@}l~mTdA^v@VZJXFZFd@FVB\\\\DF@`ATrAX^L\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.116910599999999,\n" +
                "                        \"lng\" : 112.5551863\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.9 km\",\n" +
                "                        \"value\" : 899\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 120\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1273518,\n" +
                "                        \"lng\" : 112.5555272\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Slight \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Raya Gn. Kawi\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-slight-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"~xpp@wg~mTr@G~AMx@Cr@ClDO|@ERApBMz@GnFe@RAXAzBUHArAO@?lC[zDa@hAM\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1193566,\n" +
                "                        \"lng\" : 112.5543629\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"12.9 km\",\n" +
                "                        \"value\" : 12893\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"18 mins\",\n" +
                "                        \"value\" : 1066\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.155633999999999,\n" +
                "                        \"lng\" : 112.449485\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eJl. Nasional III\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"|jrp@ao~mTThAj@|CVpALl@DPTbABRZ`B|@fFDNv@hEJh@Jj@Nz@R~@@FVfAJ^XfA@BBJ^z@Zx@DHl@vANZ^n@PVt@dAxAzBfA~ALPh@x@\\\\f@PT@@t@|@d@d@f@p@PXb@p@j@bA\\\\l@FJf@|@^p@\\\\j@hBdDJPjAnBNZ^n@bAlBP\\\\~@bBj@dATd@pBtDfAbCfA`C\\\\r@xA`D`@dAVh@j@tATh@p@~AZ~@TdAXrA@B\\\\pBTbBPbAZpAh@bBRh@^p@`@n@p@|@PRx@dAVZNTTVt@~@pA~Aj@p@rAbBZ`@jDhEl@t@b@h@bAlAb@h@FHr@z@`AlAXXtBhCtBlCx@bABDTZb@j@l@v@h@p@VXFDVRRTd@b@h@f@VTNRVZLPRXPZFNHPBFRd@DJN\\\\Xn@NXTl@NZVp@P`@HPJX`@|@P^P\\\\b@`Ab@fAZjABJ@D`@jBb@rB`@fBH\\\\h@zB@Dd@tB\\\\|An@vC`@~ALl@XjAVnA\\\\~A`@jBTbAT`AXtA`@bBjBnIFRJd@^jAv@`Cz@nCn@vBr@rBHX`@nA|@lCpA`EL^n@pBZdAh@`BBHl@nBJZZhAFTFZJj@Df@@fAAV?X?FCj@A\\\\?j@?fAOzGApAMzFA\\\\?ZGrFAVAl@ApAE~ACpA?JCr@A^Cb@?HCd@El@KxAMzAIhAGt@Cf@WdDUvCARMtBClACz@?HAzAC`B?|@AhAAx@AjBC`DArB?~@AhCExA@f@@z@B`ABZT|Ef@vFFr@F|@@h@Z~H?F?L@F?x@?n@@l@?D?R?d@?Z?F?X?f@?`@?j@@v@?H@^?r@?D@T?Z?B@\\\\?ZB`@Bj@ZdDVnBXbCf@bDLz@n@lEL|@ZtBF`@VbBBNZlBN|@L|@PhAVxAF^Ff@PbAJp@Lt@@B@BRdAThARv@Nt@BJZpA@B^|ALf@F\\\\BF@PBTDj@BXDr@B\\\\@b@@X?XCREXABI`@GVEJCFGJWZ{@z@q@r@{@z@i@f@QTQPs@t@[Xg@`@[\\\\QPIDkDv@g@Ja@HWFYJYRQVKXET[dC[bCSzA\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1273518,\n" +
                "                        \"lng\" : 112.5555272\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.3 km\",\n" +
                "                        \"value\" : 1301\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"4 mins\",\n" +
                "                        \"value\" : 242\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.144622699999999,\n" +
                "                        \"lng\" : 112.4508576\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"t{wp@gximTOE_C_@WIg@WQM_@WqB{A]UUKOEKCKEo@KaB]]GgF_AUE_AQi@IuA[uBa@_AQy@M[E_@AI@ODIDGDUDuC\\\\eD`@wDp@{Ch@g@PQFc@P\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.155633999999999,\n" +
                "                        \"lng\" : 112.449485\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"2.3 km\",\n" +
                "                        \"value\" : 2347\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 166\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1321727,\n" +
                "                        \"lng\" : 112.4398031\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e onto \\u003cb\\u003eJl. Kembar\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"zvup@{`jmTg@UUIIEUOc@g@OEa@WGEMEuCLO?gBHa@BS?uDJG?kADiBDM?K?u@@s@BY?oADy@@e@@mEJiCHi@NYPi@Xk@h@IHWZeCnCOPO^GNG\\\\EVCVCXANAJU|E_@nHEv@SdEG`ACLKZQXWTYP{BjAi@XA?OLk@j@W\\\\s@jBUl@O\\\\M^KTELCNAN?V@l@?@Fr@FtADrA@t@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.144622699999999,\n" +
                "                        \"lng\" : 112.4508576\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"2.4 km\",\n" +
                "                        \"value\" : 2442\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 170\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.136225,\n" +
                "                        \"lng\" : 112.4198682\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Kembar\\u003c/b\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"`isp@w{gmTHv@Fp@Dj@A^C\\\\E^Gd@EVABKz@MnAIh@AV@d@BRBTF^lCvIfErNBNCbGAzDAPAbDARA~CAvB?x@AfA?D?pEGbBApA?N@H?F@D@JFNDJFJJLJJJHNHVLRJ\\\\JbAVdCf@ZHLDVJHDJFLHNNRXP^j@dBn@dBVp@J`@HZDX@LAFEV?@CHGNIP_@`A]v@[v@CJCHCDAHGV\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1321727,\n" +
                "                        \"lng\" : 112.4398031\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"10.3 km\",\n" +
                "                        \"value\" : 10306\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"16 mins\",\n" +
                "                        \"value\" : 958\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.143065999999999,\n" +
                "                        \"lng\" : 112.3431298\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Slight \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Nasional III\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-slight-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"jbtp@e_dmTLr@T`BD~@@Z?B?F?BADAFCJEJGNOZGJ}AnCO\\\\M^K`@GVSjAOx@Gp@E`CEjCDfBIfA_AfB_@`AGb@AHCV?P?F?P@j@?F@NBJVz@bBjGBF@BBFDDFFDDHDHDrA`@ZH^LTHLHJHNRL\\\\BJ@J@HAJ?LGlCMzACLCLCNEJGLyB|Cc@n@EHAFCJCHCZAB?@?DAH@H@NBNVt@l@|BfBxHJnA@`@CbAArAAHCRCHAFADCDEFMLw@n@g@ZMFMFy@ZMBG@G@E@]B[BG?I@SBK@MBMBKDIBKFe@`@uBhBsAhAgBxAYXYZED[p@A@CHCJAB?DAD?H?b@C|@@RBtA?p@Af@AXCTOl@K^sAdEOb@INGLGHEDEDGBE?E?G@CAC?EAo@SaA[SIGCGAEAGAEACAGAC?CAOAE?I?I@MHGFIFGHEJCJELAL?J@HDPP^LVFPFNFPLj@Ll@J`@FJDHDFVNTNPFVFp@HNBNDPHNFHHFFLRP^HLHFFFRJXNPLJLBD@DBDBHBJDV?FB^BjADrB@z@Dx@HbCDX?HHb@N|@HZFNFHDFFD@BRJz@\\\\\\\\LzBz@RHfBn@nBx@^Rf@Xz@l@dBnA@@n@d@RNfAz@fBpAJFJDf@Th@Rh@RXNJFFDDDFFDFBDBBBFDH@@DLHTJb@Pv@Ln@n@jCVlAR|@h@~BBJL`@V~@p@dBFPb@dA^|@bDjJFPlBfGbA`DDLTt@Lh@RjA^`BVt@^z@r@nAvBjETf@Rh@`@lAl@xBx@~CNn@Hb@Lp@Jf@X~@@BjDnJv@bCJTDNDV@X?zA?^?N?vCBbA?P@dDAt@AdAC`@AHAFGp@Gp@It@Ip@C`@CTa@|GIpCKrCK|CMfCDRDPd@`BJZDT?LENADCDCDE@{@d@QHMFKFGBABGHEJCJ?J?DBDDNHLXR\\\\Zd@^JJJPHVFXB`@?Z?\\\\AFAVATCRGVMTKVAHAHAF?F@J?F?D@H?FAHADO^g@hAo@`BeAtCSp@Qd@Sp@O`@AFUt@eAbCo@nA[n@OVCDUZe@h@y@|@ONIPIPGNKVQVKHIFKDSHSFg@LA@ABCBAF?B?BBJ@DBB@@B@LFfATVFH@DDHFFLFRT`AXdAJVHRRTVRbAXp@T`@LPFLJDBBF@D@J?HADCHEFEBGBKBMBo@Ls@LYFI@EBQFIDOJa@f@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.136225,\n" +
                "                        \"lng\" : 112.4198682\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.1 km\",\n" +
                "                        \"value\" : 1062\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 124\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.143096099999999,\n" +
                "                        \"lng\" : 112.3345676\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Hasan Ahmad\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"dmup@q_ulTd@@R@T@j@D\\\\@@\\\\?XEj@ALMxBGbAE`@KdAEd@Ix@E`@?@E`@Ef@Iz@Eb@Iz@CP?J@JDPBFTLNHPJVLBxA@Z@L?X@b@?j@Aj@C\\\\Kt@Mx@SjAEP_@nBQ|@_@tB\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.143065999999999,\n" +
                "                        \"lng\" : 112.3431298\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.2 km\",\n" +
                "                        \"value\" : 1205\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 104\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.143800799999999,\n" +
                "                        \"lng\" : 112.3257959\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"jmup@ajslTlDJ|AF`CJPAFB@@@BBB?@?@@HAP?d@APEbAANCb@Gr@EtAH`AB|@ATAXMbAWbC?BY|Bk@vDSpAgBpLa@bCGX\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.143096099999999,\n" +
                "                        \"lng\" : 112.3345676\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"4.1 km\",\n" +
                "                        \"value\" : 4132\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"6 mins\",\n" +
                "                        \"value\" : 354\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.132131599999999,\n" +
                "                        \"lng\" : 112.2920483\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Raya Selopuro\\u003c/b\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"vqup@gsqlTi@nCWnBAPAj@@R?NATKbBkAvHgA|JI~@InAANGbBUjDGnAObDOlBCTEPKd@KZELCPCTE\\\\E|@Cd@APAPCPAJCJKZABCHM\\\\GRUt@Oh@Un@GTQ^Mh@_@vAuCtKe@zAQh@GPMb@Uj@k@xA]hAUr@GVGTCLOj@UjAo@hC]vAS~@Oj@uCbMmAfF_@|Aa@lB]dBADY|AK`@ALKd@Q|@WlAAJ_@dBWhAk@lCK\\\\GRGTMb@IXSt@k@vBCHcAzDOj@_@vAg@`B{A`GWx@ENKLKDA?C?E?MAgFuAQCKAKA\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.143800799999999,\n" +
                "                        \"lng\" : 112.3257959\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"2.2 km\",\n" +
                "                        \"value\" : 2152\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 207\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1222563,\n" +
                "                        \"lng\" : 112.2829929\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"xhsp@i`klTGFCFAFYnAOp@UdA{A|GMj@Ov@CFo@rCYnAMj@Ol@]tBAFCFCFCDEDA@A?E@C?GAA?WMCAMAEAE@KBC@A?IDKDC@A@C@A?C?A?A?A?C?AAAA[c@SUQSq@k@ECCAECEAECEAE?EAOAGAE?C?E?I@E@C@A@A@ABA?ENOd@[fAa@fB[pAMl@GXGXCJAHAJ?L?J?F?F?@@H@F@F@HBJJ^BHBF@D?B@@?@?F?@?D?D?D?DAFALCRKp@SrAIr@SvAUvBMdAEVELEHO?IAOAyAWmBc@mA[i@OSE_AWe@MWGg@Mq@QiHkBaBc@EA\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.132131599999999,\n" +
                "                        \"lng\" : 112.2920483\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.4 km\",\n" +
                "                        \"value\" : 1353\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 121\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1205856,\n" +
                "                        \"lng\" : 112.2718678\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"bkqp@ugilT}@dFu@`EYdBKn@_@~B[`CKbAIt@G~@QbBE^CPEZMl@Qv@g@nBWbA_@zA_@vAuAnFo@vCARCZ^TXJzAn@|Aj@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1222563,\n" +
                "                        \"lng\" : 112.2829929\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"5.9 km\",\n" +
                "                        \"value\" : 5922\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"9 mins\",\n" +
                "                        \"value\" : 553\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.130887699999999,\n" +
                "                        \"lng\" : 112.2200235\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"t`qp@ebglT@n@?hBMfHInE?NE|@IhAIr@ObAIh@Ib@WxAUlACNMt@Id@?HCRA\\\\AVAXAd@C`@Eb@Ed@AV?@?V@f@@j@@r@BXDTF^HVHXt@fAbA|ApAjBFHnArBtA~BZl@FN@BTdARx@XvAVpALv@Lt@FT`AxEv@dE\\\\|Af@lCd@~Bh@vC`@dCP`AXxADX^xBV|AXfB`@jBX|AbB|IJ\\\\RjAPv@Jp@TvAPbAXvAXdBH`@H\\\\FZFZV~ADXRvAFd@R~AHp@T~B@DDb@Hx@TrBF`@Dp@JlAFh@@VB\\\\@X?RAj@CnAExDC~B?l@@X@J@NDZHn@J|@NfARpANlAZtBl@vEPlAh@rDVlBBLTbBHd@TbBNhARtADVbAhH@Jh@tDD\\\\h@jDv@xFPhAPpAt@~FLdA^bCBH@H?D?DADCF\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1205856,\n" +
                "                        \"lng\" : 112.2718678\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"3.5 km\",\n" +
                "                        \"value\" : 3471\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"6 mins\",\n" +
                "                        \"value\" : 337\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1189334,\n" +
                "                        \"lng\" : 112.1912127\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"At the roundabout, continue straight onto \\u003cb\\u003eJl. Raya Tlogo\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"roundabout-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"`asp@c~|kT@?@@@??@@@?@?@?@?@?@?@A@?@A??@A?A?Md@Mj@}@`DCJYbAi@fBCNW`AUt@CJOf@Md@i@rBGTSp@YbAIZMd@}@bD_BjGU^Ur@IZU~@Sp@Yp@Up@[x@e@hAUj@]r@gBzC}BtDS\\\\{@zAy@xAS\\\\]l@OXOXKXKVGPELELGRENENCNIb@In@Gd@G`@CP_@xCm@bEKp@MpASjAGb@Kr@Id@_@fBCNeAhGQfAg@pCk@`DIb@UfACJEJUr@KXO`@Uj@e@fA[t@m@nAUd@kA~BcBpDKVyF~L\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.130887699999999,\n" +
                "                        \"lng\" : 112.2200235\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.6 km\",\n" +
                "                        \"value\" : 1578\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 174\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1136143,\n" +
                "                        \"lng\" : 112.1780301\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Manado\\u003c/b\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"hvpp@ajwkTq@`BQb@qCfGWh@s@xAADcAzBg@bA?@oAtCqBxEUn@CHG^Eb@I|@ADUhCOzAGb@In@Kf@Qv@_@tA_@xAENOj@K^YdAi@xBe@hB]lA{@fDe@xB\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1189334,\n" +
                "                        \"lng\" : 112.1912127\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.3 km\",\n" +
                "                        \"value\" : 1314\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"3 mins\",\n" +
                "                        \"value\" : 163\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1093943,\n" +
                "                        \"lng\" : 112.1669033\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJl. Bali\\u003c/b\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"`uop@uwtkTo@xBg@lBQp@[lA}@nD]zAu@lCa@zAW`Ak@vBk@xBa@xAU|@Md@aArDCFYlAW~@_@nAABENY`AGRs@pCKZW|@Oh@ABKZILEFCBEDIF\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1136143,\n" +
                "                        \"lng\" : 112.1780301\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.6 km\",\n" +
                "                        \"value\" : 575\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 95\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.104589499999999,\n" +
                "                        \"lng\" : 112.1678438\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"At the roundabout, take the \\u003cb\\u003e3rd\\u003c/b\\u003e exit onto \\u003cb\\u003eJl. Veteran\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"roundabout-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"tznp@crrkT@@@??@?@@??@?@?@?@?@?@?@?@A??@?@A??@A@A@?@A?A@A?A??@A?A?A?A?A?A?AAA?A?AAA??AA??AAA?AA??A?AA??A?A?A?A?A?AIA{@Kg@IYE]Ea@G_@G}@MkBWsC]y@Kc@EOC[EaDc@eAM\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1093943,\n" +
                "                        \"lng\" : 112.1669033\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"1.0 km\",\n" +
                "                        \"value\" : 956\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"2 mins\",\n" +
                "                        \"value\" : 120\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1030005,\n" +
                "                        \"lng\" : 112.1593122\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e onto \\u003cb\\u003eJl. Nasional III\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"t|mp@_xrkT[pCIl@?DA@WbCa@pDAHKjAEl@a@pDo@rFQdBa@vDm@`FQrBAL\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.104589499999999,\n" +
                "                        \"lng\" : 112.1678438\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.3 km\",\n" +
                "                        \"value\" : 305\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 76\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.102466,\n" +
                "                        \"lng\" : 112.1565909\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue straight onto \\u003cb\\u003eJl. Cempaka\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"straight\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"vrmp@ubqkTw@pHKx@e@rD\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1030005,\n" +
                "                        \"lng\" : 112.1593122\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"59 m\",\n" +
                "                        \"value\" : 59\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 12\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.1023443,\n" +
                "                        \"lng\" : 112.1560728\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Continue onto \\u003cb\\u003eJalan Cempaka\\u003c/b\\u003e\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"lomp@uqpkTYfB\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.102466,\n" +
                "                        \"lng\" : 112.1565909\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"2.6 km\",\n" +
                "                        \"value\" : 2584\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"6 mins\",\n" +
                "                        \"value\" : 356\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.093441199999999,\n" +
                "                        \"lng\" : 112.1346792\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"At the roundabout, continue straight onto \\u003cb\\u003eJalan Kediri\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"roundabout-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"rnmp@mnpkT@?@?@??@@??@@??@?@@??@?@?@?@?@A??@?@A??@A?A@A?A?A?Gx@QnCM`A]fCw@hGCPMhAIf@c@`CQbAY`BYhAMb@GVi@dBAD?@a@nAKXiAfDGNiAnCSf@_A~Bw@jBQ`@GNy@xBYn@y@vBKTWv@i@hAOZEHGLINGHA@IHGFGDMNGHCDCDEFABADCFADCLADK~@CJYp@e@hAAB{ArEWl@sA|Cg@jAa@fAi@tAo@`BUr@Sh@Yp@i@rA_@z@g@jAi@jAaAxB\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.1023443,\n" +
                "                        \"lng\" : 112.1560728\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.2 km\",\n" +
                "                        \"value\" : 230\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 25\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.095424599999999,\n" +
                "                        \"lng\" : 112.1340821\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"~vkp@whlkTJBVFv@PdCd@hDr@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.093441199999999,\n" +
                "                        \"lng\" : 112.1346792\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.5 km\",\n" +
                "                        \"value\" : 453\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 52\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.093513699999999,\n" +
                "                        \"lng\" : 112.1304508\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eright\\u003c/b\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-right\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"jclp@_elkTQp@Q`@Sd@e@hA[n@KVe@`AABOZo@xA_AzBs@|A[r@\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.095424599999999,\n" +
                "                        \"lng\" : 112.1340821\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                     \"distance\" : {\n" +
                "                        \"text\" : \"0.1 km\",\n" +
                "                        \"value\" : 131\n" +
                "                     },\n" +
                "                     \"duration\" : {\n" +
                "                        \"text\" : \"1 min\",\n" +
                "                        \"value\" : 24\n" +
                "                     },\n" +
                "                     \"end_location\" : {\n" +
                "                        \"lat\" : -8.094609999999999,\n" +
                "                        \"lng\" : 112.1300221\n" +
                "                     },\n" +
                "                     \"html_instructions\" : \"Turn \\u003cb\\u003eleft\\u003c/b\\u003e\\u003cdiv style=\\\"font-size:0.9em\\\"\\u003eDestination will be on the left\\u003c/div\\u003e\",\n" +
                "                     \"maneuver\" : \"turn-left\",\n" +
                "                     \"polyline\" : {\n" +
                "                        \"points\" : \"lwkp@inkkTtEnAB@@B\"\n" +
                "                     },\n" +
                "                     \"start_location\" : {\n" +
                "                        \"lat\" : -8.093513699999999,\n" +
                "                        \"lng\" : 112.1304508\n" +
                "                     },\n" +
                "                     \"travel_mode\" : \"DRIVING\"\n" +
                "                  }\n" +
                "               ],\n" +
                "               \"via_waypoint\" : []\n" +
                "            }\n" +
                "         ],\n" +
                "         \"overview_polyline\" : {\n" +
                "            \"points\" : \"blro@illnT~CcB|@]W_AtCwArEoB|@SdL_@fKhAxHPxBa@pBwAnBkBdAY|EbAhIPdJ]lEwCx@i@zB@fHnAfATz@x@eAfGeA`HrDjAlCvA|AlCvAjBxClCzGzExD~C~FdAf[nHxYfHzQpEjERhEKrLeBvOuDzOsDxHw@xWt@dKbAbOvEzS`Hr_@xLnYtJtQrIdRhKdIhCjc@tIlWpGdRlGfi@hShy@t\\\\vZvQvq@bTxp@dX~RbHzKpD`A\\\\[dB}@nGHlBlJjPrFlJdHjOvB`Ml@xBpBtAdKjCzBXnDfBfDfDI`C_GbTpHjKlEvD`VrIdVzIrHlKrD`ErAb@rBZzDv@lFKdVyAhSuB|Ine@dC~HtInNpFhHdIpNhIbOhMfX~EnPlB|IfChE|HtJ|MpP|TdXdDdEvB`FtCvGpEnPrL`i@bI~YhIdW~C~K?~G_Ahk@cCz`@Q`WXbUvApV@zFNhNdJ~o@|E`Vd@dFYbEqM~MuFlA}A~@iAxHc@tAwCi@kEyCoAk@yNoCuLmBgUrDy@XkACyAgAgAi@oHZwMX_Sr@wH|Hw@pJaAnRy@xAaEvBgCbEkAnDb@rKa@zPtIjY?rGGdKOfXZzA`EjBjFzA~DdKUtBoBnFh@fGyCdGu@dDc@xJCnD_BhDMvAbCpLp@r@dDbAv@dASrH{DvGMpAjArErBhKAdBKzBMZyEjCyBRmHnFgE~EAdFGhC_CvHy@v@cD_Ao@MqAb@FzBtArEdDnAlBjBbBrA^zGz@zKlCbBfJnDhFjDtHhFtCzAn@dBvDlPnL`]xEnPtElJxCvKnHjUPhLQxK{AtWMjIv@`DqBzAg@bAhCpCNbEk@|BOzAiFtNwF|KoCzDmBv@Lh@vCvAdArD`DxAnAhAcBt@kD|AzBJ^^EdA]lFa@fEi@dG`@r@`@TZfBDfBQjDgAfGq@rDjGR|CNDJU~Fa@pMmHve@cDp[qCn]c@lE_BdF}L~a@sOnr@iJt_@_E`MqHoAoId`@gB@g@P}@}@eBqA}@CeDzL^bDyAxM_@tBcC[_LqCcMcDyC|PuBpQ{FxUq@jDZp@tBz@~AzAWpRi@~F}AjJM~MpAxClFfIzB`EzAhHhGn[nErW|Ile@xDd]InPdAxI`Kjt@vF|a@?JeBdGmL~b@kD~JiMnUwBpEy@jEuJ|m@qB~G{P|^oKvUwE`L[jCwBtNiElPqQpr@uInZWVU[w[eE}Hht@yBlSIRqCxVsDzP_F`N_LfX}A|DcRve@kBdEb@J|Dv@hDr@Qp@e@fAaAxBcAxB_EfJzEtA\"\n" +
                "         },\n" +
                "         \"summary\" : \"Jl. Nasional III\",\n" +
                "         \"warnings\" : [],\n" +
                "         \"waypoint_order\" : []\n" +
                "      }\n" +
                "   ],\n" +
                "   \"status\" : \"OK\"\n" +
                "}\n";
    }
}
