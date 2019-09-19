/**
 *
 */
package org.core.client.servlet;

import org.core.common.enums.CookiesName;
import org.core.common.utils.CookieManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author piotrek Servlet odpowiedzialny za akcj� wylogowania.
 */
public class OAuthLogoutAction {
    private static final List<CookiesName> cookiesToRemove;

    static {
        cookiesToRemove = Arrays.asList(CookiesName.AppId, CookiesName.OAuthUserId,
                CookiesName.AccessToken);
    }

    /**
     * Metoda procesuj�ca wylogowanie po stronie klienta.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException ServletException
     * @throws IOException      IOException
     */
    public static void doProcess(final HttpServletRequest request, final HttpServletResponse response)
            throws ServletException, IOException {
        for (final CookiesName name : cookiesToRemove) {
            CookieManager.removeCookie(request, response, name);
        }
        //request.getRequestDispatcher("http://oauthgate.com:8080/server/oauth/logout").forward(request, response);
    }
}