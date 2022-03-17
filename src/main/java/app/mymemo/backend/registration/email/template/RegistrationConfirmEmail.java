package app.mymemo.backend.registration.email.template;

/**
 * Provides an email template to be used after registration.
 * The email contains an email(account) confirmation link
 * containing confirmation token.
 *
 * Author: Erkam Guresen
 */
public class RegistrationConfirmEmail {

    /**
     * Provides an email template to be used after registration.
     *  The email contains an email(or account) confirmation link
     *  containing confirmation token.
     *
     * @param name Name of the user. It is used to hail the user.
     * @param link the active confirmation link, which will be clicked to
     *             confirm the email (or account), contains the confirmation
     *             token as url parameter.
     * @return a email body with a hail to the name and an active email
     * (or account) confirmation link inside it.
     */
    public static String buildRegistrationConfirmEmail(String name, String link){
        // Make first Char Uppercase and the rest lowercase
        name = name.toLowerCase();
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        return "<div\n" +
                "      style=\"\n" +
                "        background-color: #f4f4f4;\n" +
                "        margin: 0 !important;\n" +
                "        padding: 0 !important;\n" +
                "      \"\n" +
                "    >\n" +
                "      <!-- HIDDEN PREHEADER TEXT -->\n" +
                "      <div\n" +
                "        style=\"\n" +
                "          display: none;\n" +
                "          font-size: 1px;\n" +
                "          color: #fefefe;\n" +
                "          line-height: 1px;\n" +
                "          font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "          max-height: 0px;\n" +
                "          max-width: 0px;\n" +
                "          opacity: 0;\n" +
                "          overflow: hidden;\n" +
                "        \"\n" +
                "      >\n" +
                ""+
                "      </div>\n" +
                "      <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "        <!-- LOGO -->\n" +
                "        <tr>\n" +
                "          <td bgcolor=\"#CC000E\" align=\"center\">\n" +
                "            <table\n" +
                "              border=\"0\"\n" +
                "              cellpadding=\"0\"\n" +
                "              cellspacing=\"0\"\n" +
                "              width=\"100%\"\n" +
                "              style=\"max-width: 600px\"\n" +
                "            >\n" +
                "              <tr>\n" +
                "                <td\n" +
                "                  align=\"center\"\n" +
                "                  valign=\"top\"\n" +
                "                  style=\"padding: 20px 10px 20px 10px\"\n" +
                "                ></td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td\n" +
                "            bgcolor=\"#CC000E\"\n" +
                "            align=\"center\"\n" +
                "            style=\"padding: 0px 10px 0px 10px\"\n" +
                "          >\n" +
                "            <table\n" +
                "              border=\"0\"\n" +
                "              cellpadding=\"0\"\n" +
                "              cellspacing=\"0\"\n" +
                "              width=\"100%\"\n" +
                "              style=\"max-width: 600px\"\n" +
                "            >\n" +
                "              <tr>\n" +
                "                <td\n" +
                "                  bgcolor=\"#ffffff\"\n" +
                "                  align=\"center\"\n" +
                "                  valign=\"top\"\n" +
                "                  style=\"\n" +
                "                    padding: 40px 20px 20px 20px;\n" +
                "                    border-radius: 4px 4px 0px 0px;\n" +
                "                    color: #111111;\n" +
                "                    font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "                    font-size: 16px;\n" +
                "                    font-weight: 400;\n" +
                "                    letter-spacing: 4px;\n" +
                "                    line-height: 16px;\n" +
                "                  \"\n" +
                "                >\n" +
                "                  <h1 style=\"font-size: 24px; font-weight: 400\">Welcome to</h1>\n" +
                "\n" +
                "                  <img\n" +
                "                    src=\"https://www.mymemo.app/logo.fw.png\"\n" +
                "                    alt=\"logo\"\n" +
                "                    width=\"100\"\n" +
                "                    height=\"100\"\n" +
                "                    style=\"\n" +
                "                      display: block;\n" +
                "                      margin-left: auto;\n" +
                "                      margin-right: auto;\n" +
                "                    \"\n" +
                "                  />\n" +
                "                  <h2 style=\"font-size: 20px; font-weight: 400\">\n" +
                "                    My Memo App !\n" +
                "                  </h2>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td bgcolor=\"#f4f4f4\" align=\"center\" style=\"padding: 0px 5px 0px 5px\">\n" +
                "            <table\n" +
                "              border=\"0\"\n" +
                "              cellpadding=\"0\"\n" +
                "              cellspacing=\"0\"\n" +
                "              width=\"100%\"\n" +
                "              style=\"max-width: 600px\"\n" +
                "            >\n" +
                "              <tr>\n" +
                "                <td\n" +
                "                  bgcolor=\"#ffffff\"\n" +
                "                  align=\"left\"\n" +
                "                  style=\"\n" +
                "                    padding: 10px 30px 20px 30px;\n" +
                "                    color: #666666;\n" +
                "                    font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "                    font-size: 16px;\n" +
                "                    font-weight: 400;\n" +
                "                    line-height: 20px;\n" +
                "                  \"\n" +
                "                >\n" +
                "                  <p style=\"margin: 0\">Hello "
                + name +
                ",</p>\n" +
                "                  <p></p>\n" +
                "                  <p style=\"margin: 0\">\n" +
                "                    We're excited to have you get started. First, you need to\n" +
                "                    confirm your account. Just press the button below. The Link\n" +
                "                    will expire in 15 minutes.\n" +
                "                  </p>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td bgcolor=\"#ffffff\" align=\"left\">\n" +
                "                  <table\n" +
                "                    width=\"100%\"\n" +
                "                    border=\"0\"\n" +
                "                    cellspacing=\"0\"\n" +
                "                    cellpadding=\"0\"\n" +
                "                  >\n" +
                "                    <tr>\n" +
                "                      <td\n" +
                "                        bgcolor=\"#ffffff\"\n" +
                "                        align=\"center\"\n" +
                "                        style=\"padding: 10px 30px 20px 30px\"\n" +
                "                      >\n" +
                "                        <table border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n" +
                "                          <tr>\n" +
                "                            <td\n" +
                "                              align=\"center\"\n" +
                "                              style=\"border-radius: 3px\"\n" +
                "                              bgcolor=\"#0A22CC\"\n" +
                "                            >\n" +
                "                              <a\n" +
                "                                href=\""
                + link +
                "\"\n" +
                "                                target=\"_blank\"\n" +
                "                                style=\"\n" +
                "                                  font-size: 20px;\n" +
                "                                  font-family: Helvetica, Arial, sans-serif;\n" +
                "                                  color: #ffffff;\n" +
                "                                  text-decoration: none;\n" +
                "                                  color: #ffffff;\n" +
                "                                  text-decoration: none;\n" +
                "                                  padding: 15px 25px;\n" +
                "                                  border-radius: 5px;\n" +
                "                                  border: 1px solid #0a22cc;\n" +
                "                                  display: inline-block;\n" +
                "                                \"\n" +
                "                                >Confirm Account</a\n" +
                "                              >\n" +
                "                            </td>\n" +
                "                          </tr>\n" +
                "                        </table>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <!-- COPY -->\n" +
                "              <tr>\n" +
                "                <td\n" +
                "                  bgcolor=\"#ffffff\"\n" +
                "                  align=\"left\"\n" +
                "                  style=\"\n" +
                "                    padding: 0px 30px 0px 30px;\n" +
                "                    color: #666666;\n" +
                "                    font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "                    font-size: 16px;\n" +
                "                    font-weight: 400;\n" +
                "                    line-height: 20px;\n" +
                "                  \"\n" +
                "                >\n" +
                "                  <p style=\"margin: 0\">\n" +
                "                    If that doesn't work, copy and paste the following link in\n" +
                "                    your browser:\n" +
                "                  </p>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <!-- COPY -->\n" +
                "              <tr>\n" +
                "                <td\n" +
                "                  bgcolor=\"#ffffff\"\n" +
                "                  align=\"left\"\n" +
                "                  style=\"\n" +
                "                    padding: 10px 30px 10px 30px;\n" +
                "                    color: #666666;\n" +
                "                    font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "                    font-size: 16px;\n" +
                "                    font-weight: 400;\n" +
                "                    line-height: 20px;\n" +
                "                  \"\n" +
                "                >\n" +
                "                  <p style=\"margin: 0\">\n" +
                "                    <a href=\""
                + link +
                "\" target=\"_blank\" style=\"color: #0a22cc\"\n" +
                "                      >"
                + link +
                "</a\n" +
                "                    >\n" +
                "                  </p>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td\n" +
                "                  bgcolor=\"#ffffff\"\n" +
                "                  align=\"left\"\n" +
                "                  style=\"\n" +
                "                    padding: 0px 30px 10px 30px;\n" +
                "                    color: #666666;\n" +
                "                    font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "                    font-size: 16px;\n" +
                "                    font-weight: 400;\n" +
                "                    line-height: 20px;\n" +
                "                  \"\n" +
                "                >\n" +
                "                  <p style=\"margin: 0\">\n" +
                "                    If you have any questions, just reply to this email—we're\n" +
                "                    always happy to help out.\n" +
                "                  </p>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "              <tr>\n" +
                "                <td\n" +
                "                  bgcolor=\"#ffffff\"\n" +
                "                  align=\"left\"\n" +
                "                  style=\"\n" +
                "                    padding: 10px 30px 20px 30px;\n" +
                "                    border-radius: 0px 0px 4px 4px;\n" +
                "                    color: #666666;\n" +
                "                    font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "                    font-size: 16px;\n" +
                "                    font-weight: 400;\n" +
                "                    line-height: 20px;\n" +
                "                  \"\n" +
                "                >\n" +
                "                  <p style=\"margin: 0\">Cheers,<br />My Memo App Team</p>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td\n" +
                "            bgcolor=\"#f4f4f4\"\n" +
                "            align=\"center\"\n" +
                "            style=\"padding: 10px 10px 0px 10px\"\n" +
                "          >\n" +
                "            <table\n" +
                "              border=\"0\"\n" +
                "              cellpadding=\"0\"\n" +
                "              cellspacing=\"0\"\n" +
                "              width=\"100%\"\n" +
                "              style=\"max-width: 600px\"\n" +
                "            ></table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "        <tr>\n" +
                "          <td\n" +
                "            bgcolor=\"#f4f4f4\"\n" +
                "            align=\"center\"\n" +
                "            style=\"padding: 0px 10px 0px 10px\"\n" +
                "          >\n" +
                "            <table\n" +
                "              border=\"0\"\n" +
                "              cellpadding=\"0\"\n" +
                "              cellspacing=\"0\"\n" +
                "              width=\"100%\"\n" +
                "              style=\"max-width: 600px\"\n" +
                "            >\n" +
                "              <tr>\n" +
                "                <td\n" +
                "                  bgcolor=\"#f4f4f4\"\n" +
                "                  align=\"left\"\n" +
                "                  style=\"\n" +
                "                    padding: 0px 30px 30px 30px;\n" +
                "                    color: #666666;\n" +
                "                    font-family: 'Lato', Helvetica, Arial, sans-serif;\n" +
                "                    font-size: 14px;\n" +
                "                    font-weight: 400;\n" +
                "                    line-height: 18px;\n" +
                "                  \"\n" +
                "                >\n" +
                "                  <br />\n" +
                "                  <p style=\"margin: 0\">\n" +
                "                    <a\n" +
                "                      href=\"https://www.mymemo.app\"\n" +
                "                      target=\"_blank\"\n" +
                "                      style=\"color: #111111\"\n" +
                "                      >My Memo App</a\n" +
                "                    >\n" +
                "                    . All Rights Reserved.\n" +
                "                  </p>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr>\n" +
                "      </table>\n" +
                "    </div>";
    }
}
